(ns hodgepodge.core
  (:require [cljs.reader :as reader]))

(enable-console-print!)
(set! *print-readably* false)

(def local-storage js/localStorage)
(def session-storage js/sessionStorage)

(def serialize pr-str)

(def deserialize reader/read-string)

(defn clear! [storage] (.clear storage))

(extend-type js/Storage
  ICounted
  (-count [s]
    (.-length s))

  ITransientAssociative
  (-assoc! [s key val]
    (.setItem s
              (serialize key)
              (serialize val)))

  ITransientMap
  (-dissoc! [s key]
    (.removeItem s
                 (serialize key)))

  ILookup
  (-lookup
    ([s key]
       (some->
        (.getItem s (serialize key))
        deserialize))
    ([s key not-found]
       (if-let [i (.getItem s (serialize key))]
         (deserialize i)
         not-found)))

  IWatchable
  (-notify-watches [this oldval newval]
    ; TODO
    )
  (-add-watch [this key f]
    ; TODO
    )
  (-remove-watch [this key]
    ; TODO
    )
)

 (comment
  (clear! local-storage)
  (clear! session-storage)

  (assert (= 0 (count local-storage)))
  (assert (= 0 (count session-storage)))

  (assoc! local-storage :foo :bar)
  (assert (= 1 (count local-storage)))
  (assert (contains? local-storage :foo))
  (assert (= 0 (count session-storage)))

  (dissoc! local-storage :foo)
  (assert (= 0 (count local-storage)))
  (assert (= 0 (count session-storage)))

  (assoc! local-storage {:foo :bar} (js/Date.))
  (assert (= 1 (count local-storage)))
  (assert (contains? local-storage {:foo :bar}))
  (assert (= 0 (count session-storage)))

  (def val {:bar 42 :timestamp {:bar (js/Date.) :baz :safd :frob #{1 2 3}}})
  (assoc! local-storage :foo val)
  (assert (= val (get local-storage :foo)))

  (assoc! local-storage val :foo)
  (assert (= :foo (get local-storage val)))

  (def date (js/Date.))
  (assoc! local-storage :date date)
)

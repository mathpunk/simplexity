(ns simplexity.core
  (:require [clojure.spec.alpha :as s]))

(s/def ::vertex nat-int?)
(s/def ::vertices (s/coll-of ::vertex :distinct true :min-count 1))

(s/exercise ::vertices)

(defprotocol Simplex
  (dim [s] "The dimension of the simplex, defined as |s|-1")
  (size [s] "The count of the vertex set of the simplex")
  (faces [s] "The subsets of the vertices in the simplex")
  )

(s/fdef dim
        :args (s/cat :integral-vertices ::vertices)
        :ret nat-int?
        :fn #(= (count (:integral-vertices (:args %)))
                (+ (:ret %) 1)))

(s/fdef size
        :args (s/cat :integral-vertices ::vertices)
        :ret nat-int?
        :fn #(= (count (:integral-vertices (:args %)))
                (:ret %)))

(extend-type clojure.lang.IPersistentCollection
  Simplex
  (dim [s] (- (count s) 1))
  (size [s] (count s))
  #_(faces [s] ))

(s/def ::simplex #(satisfies? Simplex %))

(defn simplex [v]
  v)

(ns simplexity.core
  (:require [clojure.spec.alpha :as s]
            [clojure.math.combinatorics :as combo]
            [clojure.set :as set]
            [clojure.spec.test.alpha :as test]))

(s/def ::vertex nat-int?)
(s/def ::vertices (s/coll-of ::vertex :distinct true :min-count 1))

(defprotocol Simplex
  (dim [s] "The dimension of the simplex, defined as |s|-1")
  (size [s] "The count of the vertex set of the simplex")
  (faces [s] "The subsets of the vertices in the simplex"))

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

(s/fdef faces
        :args (s/cat :integral-vertices ::vertices)
        :ret seq?
        :fn (s/and #(= (int (Math/pow 2 (count (-> % :args :integral-vertices))))
                       (count (:ret %)))
                   #(every? (fn [face] (s/valid? ::simplex %)) (:ret %))))

(extend-type clojure.lang.IPersistentCollection
  Simplex
  (dim [s] (- (count s) 1))
  (size [s] (count s))
  (faces [s] (combo/subsets s)))

(s/def ::simplex #(satisfies? Simplex %))

(defn simplex [v]
  v)

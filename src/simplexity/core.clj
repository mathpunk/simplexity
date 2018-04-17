(ns simplexity.core
  (:require [clojure.spec.alpha :as s]
            [clojure.math.combinatorics :as combo]
            ;; [clojure.test.check.impl]
            [clojure.set :as set]
            [clojure.spec.test.alpha :as test]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::vertex nat-int?)
(s/def ::vertices (s/coll-of ::vertex :distinct true :min-count 1))

(defprotocol Simplex
  (size [s] "The count of the vertex set of the simplex")
  (dim [s] "The dimension of the simplex, defined as size-1")
  (faces [s] "The subsets of the vertices in the simplex")
  (facets [s] "The maximal faces of a simplicial complex. I.e. the faces that are not members of any faces themselves"))

(declare simplex)

(s/def ::simplex (s/with-gen #(satisfies? Simplex %)
                   #(gen/fmap simplex (s/gen ::vertices))))

(s/fdef size
        :args (s/cat :simplex ::simplex)
        :ret nat-int?
        :fn #(= (count (-> % :args :simplex))
                (:ret %)))

(s/fdef dim
        :args (s/cat :simplex ::simplex)
        :ret nat-int?
        :fn #(= (size (-> % :args :simplex))
                (+ (:ret %) 1)))

(s/fdef faces
        :args (s/cat :simplex ::simplex)
        :ret seq?
        :fn (s/and #(= (int (Math/pow 2 (size (-> % :args :simplex))))
                       (count (:ret %)))
                   #(every? (fn [face] (s/valid? ::simplex %)) (:ret %))))

(extend-type clojure.lang.IPersistentCollection
  Simplex
  (dim [s] (- (count s) 1))
  (size [s] (count s))
  (faces [s] (combo/subsets s))
  (facets [s] (seq [s])))

(defn simplex [v]
  v)

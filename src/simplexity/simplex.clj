(ns simplexity.simplex
  (:require [clojure.math.combinatorics :as combo]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::vertex nat-int?)
(s/def ::vertices (s/coll-of ::vertex :distinct true :min-count 1))

(defprotocol Simplex
  (size [s] "The count of the vertex set of the simplex")
  (dim [s] "The dimension of the simplex, defined as size-1")
  (faces [s] "The subsets of the vertices in the simplex")
  (facets [s] "The maximal faces of a simplicial complex. I.e. the faces that are not members of any faces themselves")
  (homology [s] "A function from integers {k} to the number of k-dimensional holes. For a simplex, homology(0) = 1, and homology(n) = 0 for n > 0")
  )

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

;; Facets are boring. Save the spec for complexes

(s/fdef homology
        :args (s/cat :simplex ::simplex)
        :ret (s/fspec :args (s/cat :dimension :nat-int?)
                      :ret nat-int?))

(extend-type clojure.lang.IPersistentCollection
  Simplex
  (dim [s] (- (count s) 1))
  (size [s] (count s))
  (faces [s] (combo/subsets s))
  (facets [s] (seq [s]))
  (homology [s] (fn [n] (if (zero? n) 1 0))))

(defn simplex [v]
  v)

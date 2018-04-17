(ns simplexity.core
  (:require [clojure.math.combinatorics :as combo]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as test]))

(s/def ::vertex nat-int?)

(s/def ::vertices (s/coll-of ::vertex :distinct true))

(s/def ::nonempty-vertex-set (s/coll-of ::vertex :distinct true :min-count 1))

(s/def ::facets (s/coll-of ::nonempty-vertex-set))

(defprotocol Complex
  (size [s] "The count of the vertex set of the complex")
  (facets [s] "The maximal faces of a simplicial complex, i.e. the faces that are not members of any faces themselves")
  (dim [s] "The dimension of the largest simplex in the complex, which is itself defined as the size of the simplex minus one")
  (faces [s] "Every subset that is present in the complex. For a simplex, this is all subsets")
  (homology [s] "A function from integers {k} to the number of k-dimensional holes. For a simplex, homology(0) = 1, and homology(n) = 0 for n > 0"))

(declare simplex)

(declare complex)

(s/def ::simplex (s/with-gen #(satisfies? Complex %)
                   #(gen/fmap complex (s/gen ::vertices))))

(s/def ::complex (s/with-gen ::facets
                   #(gen/fmap complex (s/gen ::facets))))

(s/fdef size
        :args (s/cat :complex ::complex)
        :ret nat-int?
        :fn #(= (count (-> % :args :complex))
                (:ret %)))

(s/fdef dim
        :args (s/cat :complex ::complex)
        :ret (s/or :nonempty nat-int?
                   :empty #(= % -1))
        :fn #(= (size (-> % :args :complex))
                (+ (:ret %) 1)))

(s/fdef faces
        :args (s/cat :complex ::complex)
        :ret seq?
        :fn (s/and #(= (int (Math/pow 2 (size (-> % :args :complex))))
                       (count (:ret %)))
                   #(every? (fn [face] (s/valid? ::complex %)) (:ret %))))

;; TODO: Facets

(s/fdef homology
        :args (s/cat :complex ::complex)
        :ret (s/fspec :args (s/cat :dimension :nat-int?)
                      :ret nat-int?))

(extend-type clojure.lang.IPersistentCollection
  Complex
  (dim [s] (- (reduce max 0 (map count s)) 1))
  (size [s] (-> s flatten set count))
  (faces [s] (combo/subsets s))
  (facets [s] (seq [s]))
  (homology [s] (fn [n] (if (zero? n) 1 0))))

(defn complex [vertices]
  vertices)

(comment "This test is failing every time:"

         (test/check `dim)

         "with: clojure.lang.MapEntry cannot be cast to java.lang.Number")

(comment "...But, this sample failing output"

         (let [s [[824 16 4 6 176 12 0 1 48 2 11]
                  [2589 2 103 74 40 658 8 252 27 13 0 62 4 14 5 1 3 317]
                  [12 3710 3 94 6 2 7 1 1557 19 2153 0 959 46 6897]
                  [2 1 0]
                  [478 5 7 0 38 509 94 14 305 126 2 1 10 54 124 119]]]
           (dim (complex s)))

         "Seems to work great. ???")

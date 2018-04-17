(ns simplexity.core
  (:require [clojure.math.combinatorics :as combo]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as test]))

(s/def ::vertex nat-int?)

(s/def ::vertices (s/coll-of ::vertex :distinct true))

(s/def ::nonempty-vertex-set (s/coll-of ::vertex :distinct true :min-count 1))

(s/def ::facets (s/coll-of ::nonempty-vertex-set :distinct true))

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

(s/def ::complex (s/with-gen #(satisfies? Complex %)
                   #(gen/fmap complex (s/gen ::facets))))

#_(s/fdef size
          :args (s/cat :complex ::complex)
          :ret nat-int?
          :fn #(= (count (-> % :args :complex))
                  (:ret %)))

#_(s/fdef dim
          :args (s/cat :complex ::complex)
          :ret (s/or :nonempty nat-int?
                     :empty #(= -1 %))
          :fn #(s/and (nat-int? (-> % :ret last))
                      (= (size (-> % :args :complex))
                         (+ (-> % :ret last) 1))))


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

(defn complex [facets]
  facets)

(defn simplex [vertices]
  (reify Complex
    (size [s] (count vertices))
    (dim [s] (- (size s) 1))
    ;; TODO: Bring back 'elements' and define 'faces' as the dim-1 elements
    (faces [s] (combo/subsets vertices))
    (facets [s] [vertices])
    (homology [s] (fn [n] (if (zero? n) 1 0)))))

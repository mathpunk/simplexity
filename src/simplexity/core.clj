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

(s/fdef size
        :args (s/cat :complex ::complex)
        :ret nat-int?
        :fn #(= (count (-> % :args :complex))
                (:ret %)))

(s/fdef dim
        :args (s/cat :complex ::complex)
        :ret (s/or :nonempty nat-int?
                   :empty #(= -1 %))
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

(comment "This check is failing every time:"

         (test/check `dim)

         "with a java.lang.ClassCastException. (This is less weird than the MapEntry cannot be cast to Number exception I had before I improved the dim spec.)"

         "These are what I think are the smallest cases it's telling me about, yet they seem to check out with the right answer."

         (dim (complex [[0] [1]]))

         (dim (complex []))

         (dim (complex [[]]))

         (dim (complex [[0 12 18 6 1069 5 114 4 2 3 30 1]
                        [1 16 4 14 54 1200 6723 5 82 22 7 15754 29 0 30 3 6954 6 6125]
                        [1 0 3]
                        [31 6 73 2 706 27 0 26 1 8]
                        [45 7 467 8 4 39 2 1 1293 0 6]
                        [0 1]
                        [976 15 18 0 7 12 5369 14 1 4 2 1980 183 813 163]
                        [0 2 4 1]
                        [14023 144 3 27 93 28 103 422 0 35 20 4 3403 53 5 300 94 1 2 1981]
                        [38 6 9 52 5 136 1 23 2 3 0 8 120 97]
                        [31 1 3 0 2]
                        [1]
                        [13 16 8 14 354 2 0 9 4 34 93 3 19 1]]))

         (dim (complex [[2 1 0]
                        [0 1]
                        [0 40 11 1008 4 2 10 167 1 24 173 3 103 61 455 5 252 13]
                        [1 20 11 65215 6 2580 9 55 61 787 2 310 3 2925 54 29 0 5 311 24]
                        [3 220 60 18 1 15 0 16 7 52 838 14]
                        [2 82 14 18 18057 70943 744 596 40 583 32062 100 4 1 376 6 0 13 9 34]
                        [4 468 23 0 33 112 2 3 64 1 16]
                        [1 67 205 4 127 281 0 2 3 511]
                        [50 3 323 7 1 4 12 938 8 0]
                        [30 94 0 12 2 1 4 3 48]
                        [0 2 1]
                        [41 24 1 14 15 0 2 5 7]
                        [1 0]
                        [0 1 4]]))

         (dim (complex [[5 6 2 30 0 1 12] [8 337 1 38 72 609 2 9 155 0 25 146 10 196 3 231] [21 5652 1 6 3 11 230 218 0 2 158 7] [1 0] [27 5 0 19 3 4 1 2] [24 13 0 2 61 4 9 3 8 14 1] [0 3 1] [0 7 1 14 2] [4 2 30 3 0 1] [9 0 6 1 12] [1] [2 6 15 0 63 1 13 3 8]]))

         )

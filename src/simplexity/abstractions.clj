(ns simplexity.abstractions
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

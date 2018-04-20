(ns simplexity.complex
  (:require [clojure.math.combinatorics :as combo]
            [clojure.spec.alpha :as s]))

(s/def ::simplex (s/coll-of integer? :distinct true))

(s/def ::strict-complex (s/coll-of ::simplex))

(s/def ::complex (s/or :simplex ::simplex
                       :complex ::strict-complex))

;; TODO: Spec to check values for simplex/complex?

(defn dim [c]
  (- (reduce max 0 (map count c)) 1))

(defn size [c] (-> c flatten set count))

(defn facets [kom] (seq kom))

(s/fdef elements
        :args (s/alt :simplex (s/cat :simplex ::simplex)
                     :complex (s/cat :complex ::strict-complex))
        :ret (s/every ::simplex))

(defmulti elements #(first (s/conform ::complex %)))

(defmethod elements :simplex
  [s]
  (combo/subsets s))

(defmethod elements :complex
  [c]
  (set (mapcat elements (facets c))))

;; NOTE: Faces are defined for a simplex, not for a complex. Use (faces (facets c)).

(declare homology star closure link)

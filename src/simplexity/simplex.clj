(ns simplexity.simplex
  (:require [clojure.math.combinatorics :as combo]
            [clojure.spec.alpha :as s]))

(s/def ::simplex (s/coll-of integer? :distinct true))

(s/fdef simplex
        :args (s/cat :integral-simplex ::simplex))

(defn simplex [vertices]
  vertices)

(s/fdef size
        :args (s/cat :simplex ::simplex)
        :ret nat-int?
        :fn #(= (count (-> % :args :simplex))
                (:ret %)))

(defn size [s]
  (count s))

(s/fdef dim
        :args (s/cat :simplex ::simplex)
        :ret (s/or :nonempty nat-int?
                   :empty #(= -1 %))
        :fn #(s/and (nat-int? (-> % :ret last))
                    (= (size (-> % :args :simplex))
                       (+ (-> % :ret last) 1))))

(defn dim [s]
  (- (size s) 1))

(defn empty-or-simplex? [element]
  (s/or :empty empty?
        :nonempty-simplex (s/valid? ::simplex element)))

(s/fdef elements
        :args (s/cat :simplex ::simplex)
        :ret seq?
        :fn (s/and #(= (int (Math/pow 2 (size (-> % :args :simplex))))
                       (count (:ret %)))
                   #(every? empty-or-simplex? (:ret %))))

(defn elements [s]
  (combo/subsets s))

(s/fdef faces
        :args (s/cat :simplex ::simplex)
        :ret seq?
        :fn (s/and #(s/or :dim-1-or-greater (= (size (-> % :args :simplex))
                                               (count (:ret %)))
                          :dim-0 (= 0 (count (:ret %))))
                   #(every? empty-or-simplex? (:ret %))
                   #(every? (fn [f] (= (- (dim (-> % :args :simplex)) 1)
                                       (dim f))) (:ret %))))

(defn v-hat
  "Returns a vector with the i-th element dropped."
  [v i]
  (vec (concat (subvec v 0 i) (subvec v (+ 1 i) (count v)))))

(defn faces [s]
  (if (= 1 (size s))
    '()
    (map vec (map (partial v-hat s) (range (count s))))))

(s/fdef facets
        :args (s/cat :simplex ::simplex)
        :ret (s/coll-of ::simplex)
        :fn #(= (-> % :args :simplex) (first (:ret %))))

(defn facets [s]
  #{s})

(s/fdef homology
        :args (s/cat :simplex ::simplex)
        :ret (s/fspec :args (s/cat :dimension nat-int?)
                      :ret nat-int?))

(defn homology [s]
  (fn [n] (if (zero? n) 1 0)))

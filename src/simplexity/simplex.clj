(ns simplexity.simplex
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.math.combinatorics :as combo]
            ;; [simplexity.abstractions :refer :all]
            [clojure.spec.test.alpha :as test]))

(declare simplex)

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

(s/fdef elements
        :args (s/cat :simplex ::simplex)
        :ret seq?
        :fn (s/and #(= (int (Math/pow 2 (size (-> % :args :simplex))))
                       (count (:ret %)))
                   #(every? (fn [element] (s/or :empty empty?
                                                :nonempty-simplex (s/valid? ::simplex element))) (:ret %))))

(defn elements [s]
  (combo/subsets s))

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

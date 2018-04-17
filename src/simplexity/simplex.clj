(ns simplexity.simplex
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.math.combinatorics :as combo]
            [simplexity.abstractions :refer :all]
            [clojure.spec.test.alpha :as test]))

(declare simplex)

(s/def ::simplex (s/with-gen #(satisfies? Complex %)
                   #(gen/fmap simplex (s/gen :simplexity.abstractions/vertices))))

(defn simplex [vertices]
  (reify Complex
    (size [s] (count vertices))
    (dim [s] (- (size s) 1))
    ;; TODO: Bring back 'elements' and define 'faces' as the dim-1 elements
    (faces [s] (combo/subsets vertices))
    (facets [s] [vertices])
    (homology [s] (fn [n] (if (zero? n) 1 0)))))

(s/fdef size
        :args (s/cat :simplex ::simplex)
        :ret nat-int?
        :fn #(= (count (-> % :args :simplex))
                (:ret %)))

(s/fdef dim
        :args (s/cat :simplex ::simplex)
        :ret (s/or :nonempty nat-int?
                   :empty #(= -1 %))
        :fn #(s/and (nat-int? (-> % :ret last))
                    (= (size (-> % :args :simplex))
                       (+ (-> % :ret last) 1))))

(s/fdef faces
        :args (s/cat :simplex ::simplex)
        :ret seq?
        :fn (s/and #(= (int (Math/pow 2 (size (-> % :args :simplex))))
                       (count (:ret %)))
                   #(every? (fn [face] (s/valid? ::simplex %)) (:ret %))))

;; TODO: Facets

(s/fdef homology
        :args (s/cat :simplex ::simplex)
        :ret (s/fspec :args (s/cat :dimension :nat-int?)
                      :ret nat-int?))

(test/check `dim)

(test/check `size)

(test/check `faces)

(test/check `homology)

(ns simplexity.simplex
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.math.combinatorics :as combo]
            [simplexity.abstractions :refer :all]
            [clojure.spec.test.alpha :as test]))

(declare simplex)

(s/def ::simplex (s/with-gen #(satisfies? Complex %)
                   #(gen/fmap simplex (s/gen :simplexity.abstractions/vertices))))

(s/fdef size
        :args (s/cat :simplex ::simplex)
        :ret nat-int?
        :fn #(= (count (-> % :args :simplex))
                (:ret %)))

(s/fdef dim
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

(defn simplex [vertices]
  (reify Complex
    (size [s] (count vertices))
    (dim [s] (- (size s) 1))
    ;; TODO: Bring back 'elements' and define 'faces' as the dim-1 elements
    (faces [s] (combo/subsets vertices))
    (facets [s] [vertices])
    (homology [s] (fn [n] (if (zero? n) 1 0)))))

#_(test/check `size)

(ns simplexity.simplex
  (:require [clojure.math.combinatorics :as combo]
            [simplexity.complex]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as test]))

(defn plex-type [p]
  (first (s/conform :simplexity.complex/complex p)))

(s/fdef simplex
        :args (s/cat :integral-simplex :simplexity.complex/simplex))

(defn simplex [vertices]
  vertices)

(s/fdef size
        :args (s/cat :simplex :simplexity.complex/simplex)
        :ret nat-int?
        :fn #(= (count (-> % :args :simplex))
                (:ret %)))

(defn size [s]
  (count s))

(s/fdef dim
        :args (s/cat :simplex :simplexity.complex/simplex)
        :ret (s/or :nonempty nat-int?
                   :empty #(= -1 %)))

(defmulti dim plex-type)

(defmethod dim :simplex
  [s]
  (- (size s) 1))

(defmethod dim :complex
  [c]
  (- (reduce max 0 (map count c)) 1))

(s/fdef facets
        :args (s/cat :complex :simplexity.complex/complex)
        :ret (s/every :simplexity.complex/simplex))

(defmulti facets plex-type)

(defmethod facets :simplex
  [s]
  (seq #{s}))

(defmethod facets :complex
  [c] (seq c))

(defn empty-or-simplex? [element]
  (s/or :empty empty?
        :nonempty-simplex (s/valid? ::simplex element)))

(s/fdef elements
        :args (s/cat :complex :simplexity.complex/complex)
        :ret (s/every :simplexity.complex/simplex)
        :fn (s/and #(>= (int (Math/pow 2 (size (-> % :args :complex))))
                        (count (:ret %)))
                   #(every? empty-or-simplex? (:ret %))))

(defmulti elements plex-type)

(defmethod elements :simplex
  [s]
  (combo/subsets s))

(defmethod elements :complex
  [c]
  (set (mapcat elements (facets c))))

#_(test/check `elements {:clojure.spec.test.check/opts {:num-tests 5}})

(s/fdef faces
        :args (s/cat :simplex :simplexity.complex/simplex)
        :ret (s/or :point empty?
                   :dim-1-or-greater (s/every :simplexity.complex/simplex))
        ;; TODO: All faces are of one fewer dimension than the :args
        #_:fn #_(s/or :point empty?
                      :dim-1-or-greater #(let [d (dim (-> % :args :simplex))]
                                           (every? (fn [face] (= d (dim face))) (-> % :ret)))))

(defn v-hat
  "Returns a vector with the i-th element dropped."
  [v i]
  (vec (concat (subvec v 0 i) (subvec v (+ 1 i) (count v)))))

(defn faces [s]
  (if (= 1 (size s))
    '()
    (map vec (map (partial v-hat s) (range (count s))))))

(s/fdef homology
        :args (s/cat :simplex :simplexity.complex/simplex)
        :ret (s/fspec :args (s/cat :dimension nat-int?)
                      :ret nat-int?))

(defn homology [s]
  (fn [n] (if (zero? n) 1 0)))

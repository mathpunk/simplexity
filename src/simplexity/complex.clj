(ns simplexity.complex
  (:require [clojure.core.match :refer [match]]
            [clojure.math.combinatorics :as combo]
            [clojure.spec.alpha :as s]))

(s/def ::vertex integer?)
(s/def ::vertices (s/coll-of integer? :distinct true))
(s/def ::facets (s/coll-of ::vertices)) ;; warning: doesn't check subsethood, assumes caller gave it maximals :shrug:
(s/def ::complex (s/or :vertex-set ::vertices
                       :facet-set ::facets))

(defn conform
  "Gives simplexes and complexes a common structure"
  [c]
  (match [(s/conform ::complex c)]
         [[:vertex-set facet]] [facet]
         [[:facet-set facets]] facets))

;; It's a simplex if after you conform it it's got count one. That's it. Any collection of vertices happens to be a simplex.
(s/def ::simplex
  (s/with-gen
    (fn [sc]
      (= 1 (count (conform sc))))
    (fn [] (s/gen ::vertices))))

(require '[cognitect.transcriptor :as xr :refer [check!]])
(check! ::simplex [1 2 3 4])
(check! ::simplex [[1 2 3 4]])
(check! false? (s/valid? ::simplex [[1 3] [1 2 3 4]]))
#_(check! ::multisimplex (s/valid? ::simplex [[1 2] [1 2 3 4]]))


;; Dimension
;; ------------------
;; In a simplex, the dimension of the space spanned by the vertices. In a complex, the maximal dimension of the simplexes it contains.

(s/def ::dimension
  (s/or :nonempty nat-int?
        :empty #(= -1 %)))

(s/fdef dim
        :args (s/cat :complex :simplexity.complex/complex)
        :ret ::dimension)

(defn dim [c]
  (- (reduce max 0 (map count (conform c))) 1))

(defn facets [sc] (seq (conform sc)))

;; Elements
;; ------------------
;; All of the simplices in the complex. They are non-empty --- if the user wants to do something with the empty simplex, they'll have to do it themselves. (It's kind of the 'universe' of some problems)

(defn empty-or-simplex? [element]
  (s/or :empty empty?
        :nonempty-simplex (s/valid? ::simplex element)))

(s/fdef elements
        :args (s/cat :complex :simplexity.complex/complex)
        :ret (s/every :simplexity.complex/simplex)
        :fn (s/and #(>= (int (Math/pow 2 (count (-> % :args :complex))))
                        (count (:ret %)))
                   #(every? empty-or-simplex? (take 100 (:ret %)))))

(defn elements [c]
  (let [facets (facets (conform c))]
    (set (mapcat (fn [f] (combo/subsets f)) facets))))

;; Reminder: This will OOM you. Exponentials that you're asking to realize. Don't do it. I know that you want to. You've been here before.

#_(test/check `elements {:clojure.spec.test.check/opts {:num-tests 2}})


;; Faces
;; ---------------------------
(defn v-hat
  "Returns a vector with the i-th element dropped."
  [v i]
  (vec (concat (subvec v 0 i) (subvec v (+ 1 i) (count v)))))

(defn faces [s]
  (let [conformed (conform s)]
    (if (= 1 (count conformed))
      (map vec (map (partial v-hat (first conformed ))
                    (range (count (first conformed )))))
      conformed)))


(declare homology star closure link)

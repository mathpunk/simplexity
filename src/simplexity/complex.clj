(ns simplexity.complex
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.math.combinatorics :as combo]))

(require '[cognitect.transcriptor :refer [check!]])

(def c [[0 1] [1 2] [2 0]])

;; (s/fdef dim
;;         :args :complex ::complex
;;         :ret nat-int?)
;; You don't have to spec everything....

(defn dim [c]
  (- (reduce max 0 (map count c)) 1))

(dim c)

(check! #{1})


(def tetra [[0 1 2 3]])

(dim tetra)

(check! #{3})

(defn size [c] (-> c flatten set count))

(size c)
(check! #{3})

(defn facets [kom] (seq kom))

(def open-triangle [[0 1] [1 2] [2 0]])

(some #{[0 1]} (facets open-triangle))
(check! #{[0 1]})

(not (some #{[0 1 2]} (facets open-triangle)))
(check! #{true})

(s/def ::simplex (s/coll-of integer?))
(s/def ::strict-complex (s/coll-of ::simplex))
(s/def ::complex (s/or :simplex ::simplex
                       :complex ::strict-complex))

(defmulti faces #(first (s/conform ::complex %)))

open-triangle
(check! ::strict-complex)

[0 1 2]
(check! ::simplex)

(defmethod faces :simplex
  [s]
  (combo/subsets s))

(defmethod faces :complex
  [c]
  (set (mapcat faces (facets c))))

(def triangle [0 1 2])

;; how how should we test faces?


;;;;;;;;;;;;;;;;;;

;; (faces open-triangle)

;; (some #{[0 1]} (faces open-triangle))
;; (check! ::simplex)

;; (defn count-subsets [c]
;;   (int (Math/pow 2 (count c))))

;; (count-subsets open-triangle)
;; (count (faces open-triangle))


;; Shit. I meant to be naming this elements. Well let's checkpoint here.



;; (defn faces [s] (combo/subsets s))

;; (defn homology [s] nil)

;; (defn complex [c] c)

;; (defn facets [ss] ss)

;; ;; The "wrapper function" thing just means `(defn make-frobber [] (let [init-val 123] (Frobber. init-val)))` or some such
;; ;; so the things you'd normally do in a constructor, you do in `make-frobber`
;; ;; (... if I understood your 'wrapper' comment correctly)
;; ;; oh my bad, I thought your reference to wrapper fns were about another conversation in another room...
;; ;; So, wrt wrapping a protocol fn... Wouldn't that just mean defining a top level function that calls the protocol fn on the object passed in? And then instrument that?

;;;;;;;;;;;;;;;;;;;;;;;;;;;; * * * * * * * ;;;;;;;;;;;;;;;;;;;;;;;;

;; (s/def ::complex (s/with-gen #(satisfies? Complex %)
;;                    #(gen/fmap complex (s/gen ::facets))))

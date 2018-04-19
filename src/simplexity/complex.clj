(ns simplexity.complex
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.math.combinatorics :as combo]
            [clojure.spec.test.alpha :as test]))

(require '[cognitect.transcriptor :refer [check!]])

;; (s/fdef dim
;;         :args :complex ::complex
;;         :ret nat-int?)
;; You don't have to spec everything....

(defn dim [c]
  (- (reduce max 0 (map count c)) 1))

(defn size [c] (-> c flatten set count))

(defn facets [kom] (seq kom))

(s/def ::simplex (s/coll-of integer?))
(s/def ::strict-complex (s/coll-of ::simplex))
(s/def ::complex (s/or :simplex ::simplex
                       :complex ::strict-complex))


;;;;; in dev: elements ;;;;;;

(defmulti elements #(first (s/conform ::complex %)))

(defmethod elements :simplex
  [s]
  (combo/subsets s))

(defmethod elements :complex
  [c]
  (set (mapcat elements (facets c))))

(elements open-triangle)
(check! (s/every ::simplex))

(def triangle [0 1 2])

(elements triangle)
(some #{[0 1 2]} (elements triangle))
(check! #{[0 1 2]})
(count (elements triangle))
(check! #{8})

(s/fdef elements
        :args (s/alt :simplex (s/cat :simplex ::simplex)
                     :complex (s/cat :complex ::strict-complex))
        :ret (s/every ::simplex))

(test/check `elements)




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

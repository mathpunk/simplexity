(ns simplexity.simplex-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as test]
            [clojure.test :refer :all]
            [simplexity.complex :refer :all]))

(comment
  "

Test that known invariants of simplexes (a special case of complexes) hold.

")

;; Faces: There's the right number

;; Faces: They're the right size

;; (deftest has-faces
;;   (let [triangle (simplex [0 1 2])]
;;     (is (= 3 (count (faces triangle))))
;;     (is (every? (fn [face] (= 2 (size face))) (faces triangle))))
;;   (is (passing `simplexity.simplex/faces)))

;; Elements: They're present
;; (deftest has-elements
;;   (let [tetrahedron (simplex [0 3 7 12])]
;;     (is (some #{[0 3 7 12]} (elements tetrahedron)))
;;     (is (some #{[0 7]} (elements tetrahedron)))
;;     (is (some #{[12]} (elements tetrahedron)))
;;     (is (some #{[]} (elements tetrahedron)))
;;     (is (passing `simplexity.simplex/elements *short-num-tests*))))

;; (deftest has-one-facet
;;   (let [triangle (simplex [0 1 2])]
;;     (is (= [0 1 2] (first (facets triangle))))
;;     (is (= 1 (count (facets triangle))))
;;     (is (passing `simplexity.simplex/facets))))

;; Skeleta: The right number, the right dimension

;; Homology: Any simplex is equivalent to a sphere. In particular, the "non-cheating" implementation, thru javaplex



;; (deftest has-the-ball-homology
;;   (let [pentachoron (simplex #{0 1 2 3 4})
;;         h (homology pentachoron)]
;;     (is (= 1 (h 0)))
;;     (is (passing `simplexity.simplex/homology))))

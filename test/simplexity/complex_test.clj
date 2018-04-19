(ns simplexity.complex-test
  (:require [clojure.spec.alpha :as s]
            [cognitect.transcriptor :as xr]
            [simplexity.complex :refer :all]
            [clojure.spec.test.alpha :as test]
            [clojure.test :refer :all]))


(deftest repl-example-interaction
  (is (nil? (xr/run "./test/simplexity/complex.repl"))))

;; (def triangle (complex [0 1 2]))
;; (def open-triangle (complex [[0 1] [1 2] [2 0]]))

;; (deftest constructed-from-integers
;;   (is (complex [[0 1 2] [3 4] [4 0]]))
;;   (is (complex [[0 1 2]]))
;;   #_(is (complex #{#{0 1 2} #{3 4} #{0 3}})))


;; (deftest has-dimension
;;   (let [triangle [[1 2 3]]
;;         wiry-tetrahedron [[0 1 2 3] [0 4] [1 5] [2 6] [3 7]]]
;;     (is (= 2 (dim triangle)))
;;     #_(is (= 3 (dim wiry-tetrahedron)))
;;     ))


(comment "repl"

         (dim [[0 1 2]])

         )

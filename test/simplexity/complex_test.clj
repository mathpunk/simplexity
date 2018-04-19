(ns simplexity.complex-test
  (:require [clojure.spec.alpha :as s]
            [cognitect.transcriptor :as xr]
            [simplexity.complex :refer :all]
            [clojure.spec.test.alpha :as test]
            [clojure.test :refer :all]))


(deftest repl-example-interaction
  (is (nil? (xr/run "./test/simplexity/complex.repl"))))

;; (deftest ordered-and-unordered
;; Yeah if you even have a complex constructor like this...
;;   (is (complex [[0 1 2] [3 4] [4 0]]))
;;   (is (complex [[0 1 2]]))
;;   #_(is (complex #{#{0 1 2} #{3 4} #{0 3}})))


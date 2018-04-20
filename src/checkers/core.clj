(ns checkers.core
  (:require [clojure.spec.test.alpha :as test]
            [cognitect.transcriptor :as xr :refer [check!]]))

(defn passing
  ([sym] (:check-passed (->> sym test/check test/summarize-results)))
  ([sym n] (:check-passed
            (test/summarize-results
             (test/check sym
                         {:clojure.spec.test.check/opts {:num-tests n}})))))

(defn true! []
  (check! #{true})) ;; better to be, truthy

(defn false! []
  (check! #{false})) ;; better to be, falsey

;;(ns simplexity.repl
;; #_(:require  [clojure.test :refer [deftest testing is]]
;;              [clojure.spec.alpha :as s]
;;              [clojure.spec.test.alpha :as test]
;;              [orchestra.spec.test :as st]))

;; (require '[simplexity.core :refer :all])

;; (complex [0 1 2])

;; (test/check `simplexity.core/dim)

;; (keys (first (test/check `simplexity.core/dim)))

;; (require '[clojure.tools.reader.edn :as edn])

;; ;; This would be useful for checkers
;; (-> (test/check `simplexity.core/dim)
;;     first
;;     #_keys ;; => (:spec :clojure.spec.test.check/ret :sym :failure)
;;     :clojure.spec.test.check/ret
;;     #_keys ;; => (:fail :failing-size :num-tests :result :result-data :seed :shrunk)
;;     #_:fail ;; failing input
;;     #_:result ;; => false. Does that mean, 'fail'?
;;     :result-data ;; => A whole lot of stuff
;;     #_keys ;; => (:clojure.test.check.properties/error)
;;     :clojure.test.check.properties/error
;;     ;; class ;; => ExceptionInfo
;;     .getData
;;     )

;; (-> (test/check `simplexity.core/dim)
;;     first
;;     #_keys ;; => (:spec :clojure.spec.test.check/ret :sym :failure)
;;     :clojure.spec.test.check/ret
;;     #_keys ;; => (:fail :failing-size :num-tests :result :result-data :seed :shrunk)
;;     #_:fail ;; failing input
;;     #_:result ;; => false. Does that mean, 'fail'?
;;     :result-data ;; => A whole lot of stuff
;;     #_keys ;; => (:clojure.test.check.properties/error)
;;     :clojure.test.check.properties/error
;;     ;; class ;; => ExceptionInfo
;;     .getData
;;     ;; keys ;; => (:clojure.spec.alpha{problems spec value args val failure}
;;     #_:clojure.spec.alpha/failure ;; => :check-failed
;;     ;; :clojure.spec.alpha/value ;; => the (args, ret) pair
;;     :clojure.spec.alpha/problems
;;     first
;;     :pred
;;     )

;; ;; The failing predicate. It's kind of hard to read uhh

;; (require '[clojure.pprint :refer [pprint]])

;; (-> (test/check `simplexity.core/dim)
;;     first
;;     #_keys ;; => (:spec :clojure.spec.test.check/ret :sym :failure)
;;     :clojure.spec.test.check/ret
;;     #_keys ;; => (:fail :failing-size :num-tests :result :result-data :seed :shrunk)
;;     #_:fail ;; failing input
;;     #_:result ;; => false. Does that mean, 'fail'?
;;     :result-data ;; => A whole lot of stuff
;;     #_keys ;; => (:clojure.test.check.properties/error)
;;     :clojure.test.check.properties/error
;;     ;; class ;; => ExceptionInfo
;;     .getData
;;     ;; keys ;; => (:clojure.spec.alpha{problems spec value args val failure}
;;     #_:clojure.spec.alpha/failure ;; => :check-failed
;;     ;; :clojure.spec.alpha/value ;; => the (args, ret) pair
;;     :clojure.spec.alpha/problems
;;     first
;;     :pred
;;     pprint
;;     )

;; ;; It looked like simplex/complex. But you got a pass by using s/and instead of and.


;; (require '[clojure.java.javadoc :refer [ javadoc ]])
;; (require '[clojure.repl :refer [doc]])





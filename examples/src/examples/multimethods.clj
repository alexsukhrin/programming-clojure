(ns examples.multimethods)

;; Living Without Multimethods
(defn my-print [ob]
  (.write *out* ob))

(defn my-println [ob]
  (my-print ob)
  (.write *out* "\n"))

(my-println "hello") ;; hello

(my-println nil) ;; java.lang.NullPointerException

(defn my-print [ob]
  (cond
    (nil? ob) (.write *out* "nil")
    (string? ob) (.write *out* ob)))

(my-println nil) ;; nil

(my-println [1 2 3]) ;; nil

(require '[clojure.string :as str])

(defn my-print-vector [ob]
  (.write *out* "[")
  (.write *out* (str/join " " ob))
  (.write *out* "]"))

(defn my-print [ob]
  (cond
    (vector? ob) (my-print-vector ob)
    (nil? ob) (.write *out* "nil")
    (string? ob) (.write *out* ob)))

(my-println [1 2 3]) ;; [1 2 3]

;; Defining Multimethods

;; (defmulti name dispatch-fn)
(defmulti my-print class)

;; (defmethod name dispatch-val & fn-tail)
(defmethod my-print String [s]
  (.write *out* s))

(my-println "stu") ;; stu

(defmethod my-print nil [s]
  (.write *out* "nil"))

;; Dispatch Is Inheritance-Aware
(defmethod my-print Number [n]
  (.write *out* (.toString n)))

(my-println 42) ;; 42

(isa? Long Number) ;; true

;; Multimethod Defaults
(defmethod my-print :default [s]
  (.write *out* "#<")
  (.write *out* (.toString s))
  (.write *out* ">"))

(my-println (java.sql.Date. 0)) ;; #<1970-01-01>

(my-println (java.util.Random.)) ;; #<java.util.Random@198e33c5

;; (defmulti name dispatch-fn :default default-value)
(defmulti my-print class :default :everything-else)

(defmethod my-print String [s]
  (.write *out* s))

(defmethod my-print :everything-else [_]
  (.write *out* "Not implemented yet..."))

;; Moving Beyond Simple Dispatch

(require '[clojure.string :as str])

(defmethod my-print java.util.Collection [c]
  (.write *out* "(")
  (.write *out* (str/join " " c)) (.write *out* ")"))

(my-println (take 6 (cycle [1 2 3]))) ;; (1 2 3 1 2 3)

(my-println [1 2 3]) ;; (1 2 3)

(defmethod my-print clojure.lang.IPersistentVector [c]
  (.write *out* "[")
  (.write *out* (str/join " " c))
  (.write *out* "]"))

(my-println [1 2 3]) ;; java.lang.IllegalArgumentException: Multiple methods match

;; bad approach
(defmulti service-charge account-level)
(defmethod service-charge ::basic [acct]
  (if (= (:tag acct) ::checking) 25 10))
(defmethod service-charge ::premium [_] 0)

;; good
(defmulti service-charge (fn [acct] [(account-level acct) (:tag acct)]))
(defmethod service-charge [::acc/basic ::acc/checking] [_] 25)
(defmethod service-charge [::acc/basic ::acc/savings] [_] 10)
(defmethod service-charge [::acc/premium ::acc/checking] [_] 0)
(defmethod service-charge [::acc/premium ::acc/savings] [_] 0)

;; When Should I Use Multimethods?
;; • Where do Clojure projects use multimethods?
;; • Where do Clojure projects eschew multimethods?

;; The Inspector
(require '[clojure.inspector :refer [inspect inspect-tree]])

(inspect-tree (System/getProperties))

(inspect-tree {:clojure {:creator "Rich" :runs-on-jvm true}})

(defn collection-tag [x] 
  (cond 
    (map-entry? x) :entry 
    (instance? java.util.Map x) :seqable 
    (instance? java.util.Set x) :seqable 
    (sequential? x) :seq 
    (instance? clojure.lang.Seqable x) :seqable 
    :else :atom))

(defmulti is-leaf collection-tag) 
(defmulti get-child 
  (fn [parent index] (collection-tag parent))) 
(defmulti get-child-count collection-tag)

;; clojure.test
(require '[clojure.test :refer [is]])

(is (string? 10)) ;; FAIL in () (NO_SOURCE_FILE:2)

(is (instance? java.util.Collection "foo")) ;; false

(is (= "power" "wisdom")) ;; false

(defmulti assert-expr (fn [form message] (first form)))

;; Counterexamples
(defn class [x]
  (if (nil? x) x (.getClass x)))

(defmulti my-class identity)
(defmethod my-class nil [_] nil)
(defmethod my-class :default [x] (.getClass x))

;; Wrapping Up

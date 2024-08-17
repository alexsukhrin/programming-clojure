(ns examples.macros)

;; Writing a Control Flow Macro
(if (= 1 1) (println "yep, math still works today")) ;; yep, math still works today

; This is doomed to fail...
(defn unless [expr form]
  (if expr nil form))

(unless false (println "this should print")) ;; this should print

(unless true (println "this should not print")) ;; this should not print

(defn unless [expr form] 
  (println "About to test...") 
  (if expr nil form))

(unless false (println "this should print")) ;; this should print ; About to test...

(unless true (println "this should not print")) ;; this should not print ; About to test...

;; Macros solve this problem,
;; (unless expr form) -> (if expr nil form)
;; (defmacro name doc-string? attr-map? [params*] body)
(defmacro unless [expr form] 
  (list 'if expr nil form))

(unless false (println "this should print")) ;; this should print

(if false nil (println "this should print")) ;; this should print

;; Congratulations, you have written your first macro.
(unless false (println "this should print")) ;; this should print

(unless true (println "this should not print")) ;; nil

;; Special Forms, Design Patterns, and Macros

;; Expanding Macros
(defmacro unless [expr form]
  (list 'if expr nil form))

;; • By quoting if, you prevent Clojure from evaluating if at macro expansion
;; time. Instead, evaluation strips off the quote, leaving if to be compiled.
;; • You don’t want to quote expr and form, because they’re macro arguments.
;; Clojure will substitute them without evaluation at macro expansion time.
;; • You don’t need to quote nil, since nil evaluates to itself.

;; (macroexpand-1 form)
(macroexpand-1 '(unless false (println "this should print"))) ;; (if false nil (println "this should print"))

(defmacro bad-unless [expr form] 
  (list 'if 'expr nil form))

(macroexpand-1 '(bad-unless false (println "this should print"))) ;; (if expr nil (println "this should print"))

(bad-unless false (println "this should print")) ;; java.lang.Exception: Unable to resolve symbol: expr in this context

(macroexpand-1 '(.. arm getHand getFinger)) ;; (clojure.core/.. (. arm getHand) getFinger)

;; (macroexpand form)
(macroexpand '(.. arm getHand getFinger)) ;; (. (. arm getHand) getFinger)

(macroexpand '(and 1 2 3)) ;; (let* [and__5579__auto__ 1] (if and__5579__auto__ (clojure.core/and 2 3) and__5579__auto__))

;; when and when-not
(unless false (println "this") (println "and also this")) ;; Wrong number of args (3) passed to: examples.macros/unless

;; (when test & body)
;; (when-not test & body)
(when-not false (println "this") (println "and also this")) ;; this ; and also this

; from Clojure core
(defmacro when-not [test & body]
  (list 'if test nil (cons 'do body)))

(macroexpand-1 '(when-not false (print "1") (print "2"))) ;; (if false nil (do (print "1") (print "2")))

;; Making Macros Simpler

;; Form
;; foo#
;; (gensym prefix?) (macroexpand form)
;; (macroexpand-1 form) (list-frag? ~@form list- frag?)
;; `form
;; ~form
;; Description
;; Auto-gensym: Inside a syntax-quoted section, create a unique name prefixed with foo.
;; Create a unique name, with optional prefix.
;; Expand form with macroexpand-1 repeatedly until the returned form is no longer a macro.
;; Show how Clojure will expand form.
;; Splicing unquote: Use inside a syntax quote to splice an unquoted list into a template.
;; Syntax quote: Quote form, but allow internal unquoting so that form acts as a template. Symbols inside form are resolved to help prevent inadvertent symbol capture. Unquote: Use inside a syntax quote to substitute an unquoted value.

;; chain reimplements Clojure's .. macro
(defmacro chain [x form] 
  (list '. x form))

(defmacro chain
  ([x form] (list '. x form))
  ([x form & more] (concat (list 'chain (list '. x form)) more)))

(macroexpand '(chain arm getHand)) ;; (. arm getHand)

(macroexpand '(chain arm getHand getFinger)) ;; (. (. arm getHand) getFinger)

;; hypothetical templating language
(defmacro chain
  ([x form] (. ${x} ${form}))
  ([x form & more] (chain (. ${x} ${form}) ${more})))

;; Syntax Quote, Unquote, and Splicing Unquote
(defmacro chain [x form] 
  `(. ~x ~form))

(macroexpand '(chain arm getHand)) ;; (. arm getHand)

;; Does not quite work
(defmacro chain
  ([x form] `(. ~x ~form))
  ([x form & more] `(chain (. ~x ~form) ~more)))

(macroexpand '(chain arm getHand getFinger)) ;; (. (. arm getHand) (getFinger))

;; Creating Names in a Macro

 ;; "Elapsed time: 0.061459 msecs"
(time (str "a" "b")) ;; "ab"

;; (bench (str "a" "b"))
; should expand to
(let [start (System/nanoTime)
      result (str "a" "b")]
  {:result result :elapsed (- (System/nanoTime) start)}) ;; {:result "ab", :elapsed 139416}

;; This won't work
(defmacro bench [expr]
`(let [start (System/nanoTime)
         result ~expr]
     {:result result :elapsed (- (System/nanoTime) start)}))

(bench (str "a" "b")) ;; java.lang.Exception: Can't let qualified name: examples.macros/start

(macroexpand-1 '(bench (str "a" "b"))) ;; (clojure.core/let [examples.macros/start (System/nanoTime)
                                       ;;   examples.macros/result (str "a" "b")]
                                       ;; {:elapsed (clojure.core/- (System/nanoTime) examples.macros/start)
                                       ;;  :result examples.macros/result})

;; work
(defmacro bench [expr]
  `(let [start# (System/nanoTime)
         result# ~expr]
     {:result result# :elapsed (- (System/nanoTime) start#)}))

(bench (str "a" "b")) ;; {:result "ab", :elapsed 83791}

;; Taxonomy of Macros

;; Conditional Evaluation
(defmacro and ([] true) ([x] x)
  ([x & rest]
   `(let [and# ~x]
      (if and# (and ~@rest) and#))))

(and 1 0 nil false) ;; nil

(or 1 0 nil false) ;; 1

(comment
  (load-file "src/inspector.clj")
  (refer 'inspector)
  (inspect-tree {:a 1 :b 2 :c [1 2 3 {:d 4 :e 5 :f [6 7 8]}]}) 
  (inspect-table [[1 2 3] [4 5 6] [7 8 9] [10 11 12]])
  
  )

;; Creating Vars
(def person (create-struct :first-name :last-name))

;; (defstruct name & key-symbols)
(defmacro defstruct
  [name & keys]
  `(def ~name (create-struct ~@keys)))

;; (declare & names)
(defmacro declare
  [& names] `(do ~@(map #(list 'def %) names)))

(#(list 'def %) 'a) ;; (def a)

(map #(list 'def %) '[a b c d]) ;; ((def a) (def b) (def c) (def d))

`(do ~@(map #(list 'def %) '[a b c d])) ;; (do (def a) (def b) (def c) (def d))

(macroexpand-1 '(declare a b c d)) ;; (do (def a) (def b) (def c) (def d))

;; Java Interop
Math/PI ;;  3.141592653589793

(Math/pow 10 3) ;; 1000.0

(def PI Math/PI) ;; #'examples.macros/PI

(defn pow [b e] (Math/pow b e)) ;; #'examples.macros/pow

;; Postponing Evaluation
(def slow-calc (delay (Thread/sleep 5000) "done!"))

(force slow-calc) ;; "done!"

;; Wrapping Evaluation
(with-out-str (print "hello, ") (print "world")) ;; "hello, world"

(defmacro with-out-str 
  [& body]
  `(let [s# (new java.io.StringWriter)] 
     (binding [*out* s#] 
       ~@body 
       (str s#))))

;; (assert expr)
(assert (= 1 1)) ;; nil

(assert (= 1 2)) ;; java.lang.Exception: Assert failed: (= 1 2)

;; Avoiding Lambdas
(defn bench-fn [f]
  (let [start (System/nanoTime)
        result (f)]
    {:result result :elapsed (- (System/nanoTime) start)}))

;; macro
(bench (+ 1 2)) ;; {:result 3, :elapsed 67417}

;; function
(bench-fn (fn [] (+ 1 2))) ;; {:result 3, :elapsed 440709}

;; Wrapping Up

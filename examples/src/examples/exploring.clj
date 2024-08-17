(ns examples.exploring)

;; Numbers
(+ 2 3) ;; 5

(+ 1 2 3 4) ;; 10

(+) ;; 0

(- 10 5) ;; -5

(* 3 10 10) ;; 300

(> 5 2) ;; true

(>= 5 5) ;; true

(< 5 2) ;; false

(= 5 2) ;; false

;; Division may surprise you
(/ 22 7) ;; 22/7

(/ 22.0 7) ;; 3.142857142857143

(quot 22 7) ;; 3

(rem 22 7) ;; 1

(+ 1 (/ 0.00001 1000000000000000000)) ;; 1.0

(+ 1 (/ 0.00001M 1000000000000000000)) ;; 1.00000000000000000000001M

(* 1000N 1000 1000 1000 1000 1000 1000) ;; 1000000000000000000000N

;; Symbols
;; Symbols name all sorts of things in Clojure:
;; • Functions like str and concat
;; • “Operators” like + and -, which are, after all, just functions 
;; • Java classes like java.lang.String and java.util.Random
;; • Namespaces like clojure.core and Java packages like java.lang

;; Collections

;; vector
[1 2 3] ;; [1 2 3]

;; list
(quote (1 2 3)) ;; (1 2 3)

;; list
'(1 2 3) ;; (1 2 3)

;; set
#{1 2 3 5} ;; #{1 3 2 5}

;; map
{"Lisp" "McCarthy" "Clojure" "Hickey"} ;; {"Lisp" "McCarthy", "Clojure" "Hickey"}

;; keyword
:foo ;; :foo

;; keywords as keys
{:Lisp "McCarthy" :Clojure "Hickey"} ;; {:Lisp "McCarthy", :Clojure "Hickey"}

;; record
(defrecord Book [title author])

;; constructor
(->Book "title" "author")

;; Strings and Characters
"This is a\nmultiline string" ;; "This is a\nmultiline string"

"This is also
a multiline string" ;; "This is also\na multiline string"

(println "another\nmultiline\nstring")

(str 1 2 nil 3) ;; "123"

(str \h \e \y \space \y \o \u) ;; "hey you"

;; Booleans and nil
(true? true) ;; true

(true? "foo") ;; false

(zero? 0.0) ;; true

(zero? (/ 22 7)) ;; false

;; There are many more predicates in Clojure—go to the REPL and type
(find-doc #"\?$")

;; Functions
(str "hello" " " "world") ;; "hello world"

(string? "hello") ;; true

(keyword? :hello) ;; true

(symbol? 'hello) ;; true

;; (defn name doc-string? attr-map? [params*] prepost-map? body)

(defn greeting
  "Returns a greeting of the form 'Hello, username.'" [username]
  (str "Hello, " username))

(greeting "world") ;; "Hello, world"

(doc greeting)

;; functions arity
(defn greeting
  "Returns a greeting of the form 'Hello, username.'
Default username is 'world'."
  ([] (greeting "world"))
  ([username] (str "Hello, " username)))

(greeting) ;; "Hello, world"

;; function args
(defn date [person-1 person-2 & chaperones]
  (println person-1 "and" person-2
           "went out with" (count chaperones) "chaperones."))

(date "Romeo" "Juliet" "Friar Lawrence" "Nurse") ;; Romeo and Juliet went out with 2 chaperones.

;; Anonymous Functions
(require '[clojure.string :as str])
;; (fn [params*] body)
(filter (fn [w] (> (count w) 2)) (str/split "A fine day" #"\W+"))

;; #(body)
(filter #(> (count %) 2) (str/split "A fine day it is" #"\W+"))

(defn indexable-words [text]
  (let [indexable-word? (fn [w] (> (count w) 2))]
    (filter indexable-word? (str/split text #"\W+"))))

(indexable-words "a fine day it is") ;; ("fine" "day")

(defn make-greeter [greeting-prefix]
  (fn [username] (str greeting-prefix ", " username)))

(def hello-greeting (make-greeter "Hello"))

(def aloha-greeting (make-greeter "Aloha"))

(hello-greeting "world") ;; "Hello, world"

(aloha-greeting "world") ;; "Aloha, world"

((make-greeter "Howdy") "pardner") ;; "Howdy, pardner"

;; When to Use Anonymous Functions
;; Anonymous functions have a terse syntax—sometimes too terse.
;; Anonymous functions are an option, not a requirement.
;; Use the anonymous forms only when you find that they make your code more readable.

;; Vars, Bindings, and Namespaces

;; Vars
(def foo 10)

foo ;; 10

(var foo) ;; #'examples.exploring/foo

;; equivalent
#'foo ;; #'examples.exploring/foo

;; Bindings
(defn triple [number] (* 3 number))

(triple 10) ;; 30

;; (let [bindings*] exprs*)
(defn square-corners [bottom left size]
  (let [top (+ bottom size)
        right (+ left size)]
    [[bottom left] [top left] [top right] [bottom right]]))

;; Destructuring
(defn greet-author-1 [author]
  (println "Hello," (:first-name author)))

(greet-author-1 {:last-name "Vinge" :first-name "Vernor"}) ;; Hello, Vernor

(defn greet-author-2 [{fname :first-name}]
  (println "Hello," fname))

(greet-author-2 {:last-name "Vinge" :first-name "Vernor"}) ;; Hello, Vernor

(let [[x y] [1 2 3]]
  [x y]) ;; [1 2]

(let [[_ _ z] [1 2 3]] z) ;; 3

(let [[_ _ z] [1 2 3]] _) ;; 2

(let [[x y :as coords] [1 2 3 4 5 6]]
  (str "x: " x ", y: " y ", total dimensions " (count coords)))
;; "x: 1, y: 2, total dimensions 6"

(defn ellipsize [words]
  (let [[w1 w2 w3] (str/split words #"\s+")] (str/join " " [w1 w2 w3 "..."])))

(ellipsize "The quick brown fox jumps over the lazy dog.") ;; "The quick brown ..."

;; Namespaces
(def foo 10)

(resolve 'foo)

(in-ns 'myapp)

String ;; java.lang.String

(clojure.core/use 'clojure.core)

File/separator ;; java.lang.Exception: No such namespace: File

java.io.File/separator ;; "/"

(import '(java.io InputStream File))

(.exists (File. "/tmp")) ;; true

(require 'clojure.string)

(clojure.string/split "Something,separated,by,commas" #",") ;; ["Something" "separated" "by" "commas"]

(require '[clojure.string :as str])

(str/split "Something,separated,by,commas" #",") ;; ["Something" "separated" "by" "commas"]

(ns examples.exploring
  (:require [clojure.string :as str])
  (:import (java.io File)))

(in-ns 'user)

(find-doc "ns-")

;; Metadata
(meta #'str)

;; see also shorter form below
(defn ^{:tag String} shout [^{:tag String} s] (clojure.string/upper-case s))

(meta #'shout)

(defn ^String shout [^String s] (clojure.string/upper-case s))

(defn shout
  ([s] (clojure.string/upper-case s))
  {:tag String})

;; Calling Java
(new java.util.Random)

(java.util.Random.)

(def rnd (new java.util.Random))

(. rnd nextInt)

(. rnd nextInt 10)

;; Instance field
(def p (java.awt.Point. 10 20))
(. p x) ;; 10

;; Static method
(. System lineSeparator) ;; "\n"

;; Static field
(. Math PI)

;; (.method instance & args)
;; (.field instance)
;; (.-field instance)
;; (Class/method & args)
;; Class/field

(import '(java.util Random Locale)
        '(java.text MessageFormat))

Random ;; java.util.Random
Locale ;; java.util.Locale
MessageFormat ;; java.text.MessageFormat

;;  Clojure provides a javadoc function that will make your life much easier.
(javadoc java.net.URL)

;; Comments

;; this is a comment

(comment
  (defn ignore-me []
    ;; not done yet
    ))

(defn triple [number]
  #_(println "debug triple" number)
  (* 3 number))

;; Flow Control

(defn is-small? [number]
  (if (< number 100) "yes"))

(is-small? 50) ;; "yes"

(is-small? 50000) ;; nil

(defn is-small? [number]
  (if (< number 100) "yes" "no"))

(is-small? 50000) ;; "no"

;; Introduce Side Effects with do

(defn is-small? [number]
  (if (< number 100)
    "yes"
    (do
      (println "Saw a big number" number)
      "no")))

(is-small? 200)

;; Recur with loop/recur
;; (loop [bindings*] exprs*)
;; (recur exprs*)

(loop [result [] x 5]
  (if (zero? x)
    result
    (recur (conj result x) (dec x)))) ;; [5 4 3 2 1]

(defn countdown [result x]
  (if (zero? x)
    result
    (recur (conj result x) (dec x))))

(countdown [] 5) ;; [5 4 3 2 1]

(into [] (take 5 (iterate dec 5))) ;; [5 4 3 2 1]

(into [] (drop-last (reverse (range 6)))) ;; [5 4 3 2 1]

(vec (reverse (rest (range 6)))) ;; [5 4 3 2 1]

;; Where’s My for Loop?
(defn indexed [coll] (map-indexed vector coll))

(indexed "abcde") ;; ([0 \a] [1 \b] [2 \c] [3 \d] [4 \e])

(defn index-filter [pred coll]
  (when pred
    (for [[idx elt] (indexed coll) :when (pred elt)] idx)))

(index-filter #{\a \b} "abcdbbb") ;; (0 1 4 5 6)

(index-filter #{\a \b} "xyz") ;; ()

(defn index-of-any [pred coll]
  (first (index-filter pred coll)))

(index-of-any #{\z \a} "zzabyycdxx")

(index-of-any #{\b \y} "zzabyycdxx") ;; 3

(nth
 (index-filter #{:h} [:t :t :h :t :h :t :t :t :h :h])
 2) ;; 8

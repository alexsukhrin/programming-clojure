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

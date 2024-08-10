(ns examples.introduction)

(defn blank? [str]
  (every? #(Character/isWhitespace %) str))

(blank? "") ;; true

(blank? "Hello") ;; false

(defrecord Person [first-name last-name])

(def person (->Person "Aaron" "Bedra"))

(:first-name person) ;; Aaron

(defn hello-world [username]
  (println (format "Hello, %s" username)))

(hello-world (:first-name person)) ;; Hello, Aaron

(def x 10)

; Clojure cond
(cond (= x 10) "equal" (> x 10) "more") ;; equal

(def compositions ["Requiem" "Hello" "World"])

(for [c compositions :when (= (:name c) "Requiem")] (:composer c))

;; For example, the following code creates a working, thread-safe, in-memory database of accounts
(def accounts (ref #{}))

(defrecord Account [id balance])

(dosync
 (alter accounts conj (->Account "CLJ" 1000.00)))

;; Clojure gives you clean, simple, direct access to Java
(System/getProperties)

;; Clojure provides simple functions for implementing Java interfaces and sub- classing Java classes
;; Also, all Clojure functions implement Callable and Runnable
(.start (new Thread (fn [] (println "Hello" (Thread/currentThread)))))

;; Using the REPL
(println "hello world")

(defn hello [name] (str "Hello, " name))

;; Special Variables
(hello "Stu")

(hello "Clojure")

(str *1 " and " *2)

(/ 1 0)

;; print stack trace
(pst)

;; save some work
(load-file "src/examples/introduction.clj")

;; Adding Shared State
#{}

(conj #{} "Stu")

(def visitors (atom #{}))

;; (swap! r update-fn & args)
(swap! visitors conj "Stu")

(deref visitors)
@visitors

(defn hello
  "Writes hello message to *out*. Calls you by username. Knows if you have been here before."
  [username]
  (swap! visitors conj username)
  (str "Hello, " username))

(hello "Rich")

@visitors ;; #{"Stu" "Rich"}

;; Navigating Clojure Libraries
(require 'clojure.java.io)

(require 'examples.introduction)

(def fibs [0  1 1 2 3 4 5 6 7 7 8])

(take 10 examples.introduction/fibs)

;; Use doc to print the documentation for str
(doc str)

(find-doc "reduce")

(source identity)

(instance? java.util.Collection [1 2 3])

;; Clojure’s complete API is documented at https://clojure.github.io/clojure

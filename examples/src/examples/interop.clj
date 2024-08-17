(ns examples.interop)

;; Creating Java Objects in Clojure

;; Direct Use of Java Types

(defn say-hi []
  (println "Hello from thread" (.getName (Thread/currentThread))))

(dotimes [_ 3]
  (.start (Thread. say-hi)))

;; Hello from thread Thread-1
;; Hello from thread Thread-2
;; Hello from thread Thread-3

(java.util.Collections/binarySearch [1 13 42 1000] 42) ;; 2

;; Implementing Java Interfaces
(import [java.io File FilenameFilter])

(defn suffix-filter [suffix] 
  (reify FilenameFilter 
    (accept [this dir name] 
      (.endsWith name suffix))))

(defn list-files [dir suffix]
  (seq (.list (File. dir) (suffix-filter suffix))))

(list-files "." ".clj") ;; ("project.clj")

(defrecord Counter [n]
  Runnable
  (run [this] (println (range n)))) ;; examples.interop.Counter

(def c (->Counter 5)) ;; #'examples.interop/c

(.start (Thread. c)) ;; (0 1 2 3 4)

(:n c) ;; 5

(def c2 (assoc c :n 8)) ;; #'examples.interop/c2

(.start (Thread. c2)) ;; (0 1 2 3 4 5 6 7)

;; Extending Classes with Proxies
(import '[org.xml.sax InputSource]
        '[org.xml.sax.helpers DefaultHandler]
        '[java.io StringReader]
        '[javax.xml.parsers SAXParserFactory])

(def print-element-handler 
  (proxy [DefaultHandler] [] 
    (startElement [uri local qname atts] 
      (println (format "Saw element: %s" qname)))))

(defn demo-sax-parse [source handler]
  (.. SAXParserFactory newInstance newSAXParser
      (parse (InputSource. (StringReader. source)) handler)))

(demo-sax-parse "<foo>
<bar>Body of bar</bar>
</foo>" print-element-handler)

;; Saw element: foo
;; Saw element: bar

;; Calling Clojure From Java
IFn plus = Clojure.var ("clojure.core", "+");
System.out.println (plus.invoke (1, 2, 3));

Object vector = Clojure.read ("[1 2 3]");

IFn require = Clojure.var ("clojure.core", "require");
require.invoke (Clojure.read ("clojure.set"));

;; Exception Handling

;; Keeping Exception Handling Simple
;; try {newManifest = new Manifest (r);
;;      }catch (IOException e) 
;;      {throw new BuildException (...);
;;                              }

;; Rethrowing with ex-info

(defn load-resource [path]
  (try
    (if (forbidden? path)
      (throw (ex-info "Forbidden resource"
                      {:status 403, :resource path}))
      (slurp path))
    (catch FileNotFoundException e (throw (ex-info "Missing resource"
                                                     {:status 404, :resource path})))
      (catch IOException e
        (throw (ex-info "Server error"
                        {:status 500, :resource path})))))


;; Cleaning Up Resources
(spit "hello.out" "hello, world") ;; nil

;; from clojure.core
(defn spit
  "Opposite of slurp. Opens f with writer, writes content, then closes f. Options passed to clojure.java.io/writer."
  {:added "1.2"}
  [f content & options]
  (with-open [^java.io.Writer w (apply jio/writer f options)]
    (.write w (str content))))

;; (try expr* catch-clause* finally-clause?)
(try
  (throw (Exception. "something failed"))
  (finally
    (println "we get to clean up"))) ;; java.lang.Exception: something failed

;; Responding to an Exception

;; not caller-friendly
(defn class-available? [class-name] 
  (Class/forName class-name))

(defn class-available? [class-name] 
  (try 
    (Class/forName class-name) true 
    (catch ClassNotFoundException _ false)))

(class-available? "borg.util.Assimilate") ;; false

(class-available? "java.lang.String") ;; true

;; Optimizing for Performance

;; Adding Type Hints

;; • Optimizing critical performance paths 
;; • Documenting the required type

(defn describe-class [c] 
  {:name (.getName c) 
   :final (java.lang.reflect.Modifier/isFinal (.getModifiers c))})

(set! *warn-on-reflection* true) ;; true

(defn describe-class [^Class c] 
  {:name (.getName c) 
   :final (java.lang.reflect.Modifier/isFinal (.getModifiers c))})

(describe-class StringBuffer) ;; {:name "java.lang.StringBuffer", :final true}

(describe-class "foo") ;; IllegalArgumentException No matching field found: getName

(defn wants-a-string [^String s] (println s)) ;; #'examples.interop/wants-a-string

(wants-a-string "foo") ;; foo

(wants-a-string 0) ;; 0

;; Integer Math
(unchecked-add 9223372036854775807 1) ;; -9223372036854775808

(+ 9223372036854775807 1) ;; ArithmeticException integer overflow

(+' 9223372036854775807 1) ;; 9223372036854775808N

;; Using Primitives for Performance
(defn sum-to [n]
  (loop [i 1 sum 0] 
    (if (<= i n) 
      (recur (inc i) (+ i sum)) 
      sum)))

(sum-to 10) ;; 55

(dotimes [_ 5] (time (sum-to 100000)))

; "Elapsed time: 11.92625 msecs"
; "Elapsed time: 3.357875 msecs"
; "Elapsed time: 3.084708 msecs"
; "Elapsed time: 4.050041 msecs"
; "Elapsed time: 10.613375 msecs"

(defn integer-sum-to ^long [^long n] 
  (loop [i 1 sum 0] 
    (if (<= i n) 
      (recur (inc i) (+ i sum)) sum)))

(dotimes [_ 5] (time (integer-sum-to 100000)))

; "Elapsed time: 5.325167 msecs"
; "Elapsed time: 0.525958 msecs"
; "Elapsed time: 0.510875 msecs"
; "Elapsed time: 0.510875 msecs"
; "Elapsed time: 0.513083 msecs"

(defn unchecked-sum-to ^long [^long n] 
  (loop [i 1 sum 0] 
    (if (<= i n) 
      (recur (inc i) (unchecked-add i sum)) 
      sum)))

(dotimes [_ 5] (time (unchecked-sum-to 100000)))

; "Elapsed time: 3.655916 msecs"
; "Elapsed time: 0.128333 msecs"
; "Elapsed time: 0.127584 msecs"
; "Elapsed time: 0.127292 msecs"
; "Elapsed time: 0.127167 msecs"

(integer-sum-to 10000000000) ;; java.lang.ArithmeticException: integer overflow

(unchecked-sum-to 10000000000) ;; -5340232216128654848 ; WRONG!!

(defn better-sum-to [n]
  (reduce + (range 1 (inc n))))

(defn best-sum-to [n] (/ (* n (inc n)) 2))

(dotimes [_ 5] (time (best-sum-to 100000)))

; "Elapsed time: 0.127875 msecs"
; "Elapsed time: 0.012625 msecs"
; "Elapsed time: 0.004416 msecs"
; "Elapsed time: 0.00375 msecs"
; "Elapsed time: 0.003792 msecs"

;; Using Java Arrays
;; (make-array class length)
(make-array String 5) ;; #object["[Ljava.lang.String;" 0x6a129a7d "[Ljava.lang.String;@6a129a7d"]

(seq (make-array String 5)) ;; (nil nil nil nil nil)

(defn painstakingly-create-array [] 
  (let [arr (make-array String 5)] 
    (aset arr 0 "Painstaking") 
    (aset arr 1 "to") 
    (aset arr2 "fill") 
    (aset arr 3 " in") 
    (aset arr 4 "arrays") arr))

(aget (paintakingly-create-array) 0)

(to-array ["Easier" "array" "creation"]) ;; (to-array ["Easier" "array" "creation"])

;; Wrapping Up

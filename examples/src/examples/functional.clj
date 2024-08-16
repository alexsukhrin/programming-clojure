(ns examples.functional)

;; Recursion

;; bad idea
(defn stack-consuming-fibo [n] (cond
                                 (= n 0) 0
                                 (= n 1) 1
                                 :else (+ (stack-consuming-fibo (- n 1))
                                          (stack-consuming-fibo (- n 2)))))

(stack-consuming-fibo 9) ;; 34
(stack-consuming-fibo 1000000) ;; StackOverflowError clojure.lang.Numbers.minus

;; Tail Recursion

;; (letfn fnspecs & body) ; fnspecs ==> [(fname [params*] exprs)+]
(defn tail-fibo [n]
  (letfn [(fib [current next n]
            (if (zero? n) current
                (fib next (+ current next) (dec n))))]
    (fib 0N 1N n)))

(tail-fibo 9) ;; 34N

;; The problem here is the JVM. While functional languages such as Scheme or Haskell perform TCO, the JVM doesn’t perform this optimization. 
;; The absence of TCO is unfortunate but not a showstopper for functional programs.
(tail-fibo 1000000) ;; StackOverflowError java.lang.Integer.numberOfLeadingZeros

;; Self-recursion with recur
;; better but not great
(defn recur-fibo [n]
  (letfn [(fib [current next n]
            (if (zero? n)
              current
              (recur next (+ current next) (dec n))))]
    (fib 0N 1N n)))

(recur-fibo 1000000) ;; 195 ... 208,982 other digits ... 875N

;; Lazy Sequences

;; (lazy-seq & body)
(defn lazy-seq-fibo
  ([]
   (concat [0 1] (lazy-seq-fibo 0N 1N)))
  ([a b]
   (let [n (+ a b)]
     (lazy-seq
      (cons n (lazy-seq-fibo b n))))))

(take 10 (lazy-seq-fibo)) ;; (0 1 1N 2N 3N 5N 8N 13N 21N 34N)

(rem (nth (lazy-seq-fibo) 1000000) 1000) ;; 875N

(take 5 (iterate (fn [[a b]] [b (+ a b)]) [0 1])) ;; ([0 1] [1 1] [1 2] [2 3] [3 5])

(defn fibo []
  (map first (iterate (fn [[a b]] [b (+ a b)]) [0N 1N])))

;; Coming to Realization
(def lots-o-fibs (take 1000000000 (fibo)))

(nth lots-o-fibs 100) ;; 354224848179261915075N

(take 1000000000 (fibo)) ;; 

;; settings
(set! *print-length* 10)

(take 1000000000 (fibo)) ;; (0N 1N 1N 2N 3N 5N 8N 13N 21N 34N ...)

;; Losing Your Head

;; holds the head (avoid!)
(def head-fibo (lazy-cat [0N 1N] (map + head-fibo (rest head-fibo))))

(take 10 head-fibo) ;; (0N 1N 1N 2N 3N 5N 8N 13N 21N 34N)

(nth head-fibo 1000000) ;; java.lang.OutOfMemoryError: GC overhead limit exceeded

;; Lazier Than Lazy

(defn count-heads-pairs [coll]
  (loop [cnt 0 coll coll]
    (if (empty? coll)
      cnt
      (recur (if (= :h (first coll) (second coll))
               (inc cnt)
               cnt)
             (rest coll)))))

(count-heads-pairs [:h :h :h :t :h]) ;; 2

(count-heads-pairs [:h :t :h :t :h]) ;; 0

;; Transforming the Input Sequence

;; overly complex, better approaches follow...
(defn by-pairs [coll]
  (let [take-pair (fn [c]
                    (when (next c) (take 2 c)))]
    (lazy-seq
     (when-let [pair (seq (take-pair coll))]
       (cons pair (by-pairs (rest coll)))))))

(by-pairs [:h :t :t :h :h :h]) ;; ((:h :t) (:t :t) (:t :h) (:h :h) (:h :h))

(defn count-heads-pairs [coll]
  (count (filter (fn [pair] (every? #(= :h %) pair))
                 (by-pairs coll))))

;; (partition size step? coll)
(partition 2 [:h :t  :t :h :h :h]) ;; ((:h :t) (:t :h) (:h :h))

(partition 2 1 [:h :t  :t :h :h :h]) ;; ((:h :t) (:t :t) (:t :h) (:h :h) (:h :h))

(by-pairs [:h :t  :t :h :h :h]) ;; ((:h :t) (:t :t) (:t :h) (:h :h) (:h :h))

;; (comp f & fs)
(def ^{:doc "Count items matching a filter"} count-if (comp count filter))

(count-if odd? [1 2 3 4 5]) ;; 3

(defn count-runs
  "Count runs of length n where pred is true in coll."
  [n pred coll]
  (count-if #(every? pred %) (partition n 1 coll)))

(count-runs 2 #(= % :h) [:h :t :t :h :h :h]) ;; 2

(count-runs 2 #(= % :t) [:h :t :t :h :h :h]) ;; 1

(count-runs 3 #(= % :h) [:h :t :t :h :h :h]) ;; 1

;; Currying and Partial Application

;; (partial f & partial-args)
(def ^{:doc "Count runs of length two that are both heads"}
  count-heads-pairs (partial count-runs 2 #(= % :h)))

(partial count-runs 1 #(= % :h))

;; equal
(fn [coll] (count-runs 1 #(= % :h) coll))

;; almost a curry
(defn faux-curry [& args] (apply partial partial args))

(def add-3 (partial + 3))

(add-3 7) ;; 10

(def add-3 ((faux-curry +) 3))

(add-3 7) ;; 10

;; faux curry
((faux-curry true?) (= 1 1))

;; if the curry were real ((curry true?) (= 1 1))
(((faux-curry true?) (= 1 1))) ;; true

;; Recursion Revisited

(declare my-odd? my-even?)

(defn my-odd? [n]
  (if (= n 0)
    false
    (my-even? (dec n))))

(defn my-even? [n]
  (if (= n 0)
    true
    (my-odd? (dec n))))

(map my-even? (range 10)) ;; (true false true false true false true false true false)

(map my-odd? (range 10)) ;; (false true false true false true false true false true)

(my-even? (* 1000 1000 1000)) ;; StackOverflowError clojure.lang.Numbers$LongOps.equiv

;; Converting to Self-recursion
(defn parity [n]
  (loop [n n par 0]
    (if (= n 0)
      par
      (recur (dec n) (- 1 par)))))

(map parity (range 10)) ;; (0 1 0 1 0 1 0 1 0 1)

(defn my-even? [n] (= 0 (parity n)))
(defn my-odd? [n] (= 1 (parity n)))

;; Trampolining Mutual Recursion

;; (trampoline f & partial-args)
(trampoline list) ;; ()

(trampoline + 1 2) ;; 3

;; Example only. Don't write code like this.
(defn trampoline-fibo [n]
  (let [fib (fn fib [f-2 f-1 current]
              (let [f (+ f-2 f-1)]
                (if (= n current)
                  f
                  #(fib f-1 f (inc current)))))]
    (cond
      (= n 0) 0
      (= n 1) 1
      :else (fib 0N 1 2))))

(trampoline trampoline-fibo 9) ;; 34N

(declare my-odd? my-even?)

(defn my-odd? [n]
  (if (= n 0)
    false
    #(my-even? (dec n))))

(defn my-even? [n]
  (if (= n 0)
    true
    #(my-odd? (dec n))))

(trampoline my-even? 1000000) ;; true

;; Replacing Recursion with Laziness

;; overly-literal port, do not use
(declare replace-symbol replace-symbol-expression)

(defn replace-symbol [coll oldsym newsym]
  (if (empty? coll) ()
      (cons (replace-symbol-expression
             (first coll) oldsym newsym)
            (replace-symbol
             (rest coll) oldsym newsym))))

(defn replace-symbol-expression [symbol-expr oldsym newsym]
  (if (symbol? symbol-expr)
    (if (= symbol-expr oldsym) newsym
        symbol-expr)
    (replace-symbol symbol-expr oldsym newsym)))

(defn deeply-nested [n]
  (loop [n n result '(bottom)]
    (if (= n 0)
      result
      (recur (dec n) (list result)))))

(deeply-nested 5) ;; ((((((bottom))))))

(deeply-nested 25) ;; (((((((((((((((((((((((((bottom)))))))))))))))))))))))))

(set! *print-level* 25)

(deeply-nested 5) ;; ((((((bottom))))))

(deeply-nested 25) ;; (((((((((((((((((((((((((#)))))))))))))))))))))))))

(replace-symbol (deeply-nested 5) 'bottom 'deepest) ;; ((((((deepest))))))

(replace-symbol (deeply-nested 10000) 'bottom 'deepest) ;; java.lang.StackOverflowError

(defn- coll-or-scalar [x & _] (if (coll? x) :collection :scalar))

(defmulti replace-symbol coll-or-scalar)

(defmethod replace-symbol :collection [coll oldsym newsym]
  (lazy-seq
   (when (seq coll)
     (cons (replace-symbol (first coll) oldsym newsym)
           (replace-symbol (rest coll) oldsym newsym)))))

(defmethod replace-symbol :scalar [obj oldsym newsym]
  (if (= obj oldsym) newsym obj))

(replace-symbol (deeply-nested 10000) 'bottom 'deepest) ;; (((((((((((((((((((((((((#)))))))))))))))))))))))))

;; Shortcutting Recursion with Memoization

;; do not use these directly
(declare m f)

(defn m [n]
  (if (zero? n)
    0
    (- n (f (m (dec n))))))

(defn f [n]
  (if (zero? n)
    1
    (- n (m (f (dec n))))))

(time (m 250)) ;; 155 "Elapsed time: 52259.334375 msecs"

(def m (memoize m))
(def f (memoize f))

(time (m 250)) ;; 155 "Elapsed time: 1.695166 msecs"
(time (m 250)) ;; 155 "Elapsed time: 0.130625 msecs"

(m 10000) ;; java.lang.StackOverflowError

(def m-seq (map m (iterate inc 0)))
(def f-seq (map f (iterate inc 0)))

(nth m-seq 250) ;; 155

(time (nth m-seq 10000)) ;; 6180 "Elapsed time: 27.55525 msecs"

(defn square [x] (* x x))

(defn sum-squares-seq [n]
  (vec (map square (range n))))

(defn sum-squares [n]
  (into [] (map square) (range n)))

(defn preds-seq []
  (->> (all-ns)
       (map ns-publics)
       (mapcat vals)
       (filter #(clojure.string/ends-with? % "?")) (map #(str (.-sym %)))
       vec))

(defn preds []
  (into []
        (comp (map ns-publics)
              (mapcat vals)
              (filter #(clojure.string/ends-with? % "?")) (map #(str (.-sym %))))
        (all-ns)))

(defn non-blank? [s]
  (not (clojure.string/blank? s)))

(defn non-blank-lines-seq [file-name]
  (let [reader (clojure.java.io/reader file-name)]
    (filter non-blank? (line-seq reader))))

(defn non-blank-lines [file-name]
  (with-open [reader (clojure.java.io/reader file-name)]
    (into [] (filter non-blank?) (line-seq reader))))

(defn non-blank-lines-eduction [reader]
  (eduction (filter non-blank?) (line-seq reader)))

(defn line-count [file-name]
  (with-open [reader (clojure.java.io/reader file-name)]
    (reduce (fn [cnt el] (inc cnt)) 0 (non-blank-lines-eduction reader))))

;; Wrapping Up

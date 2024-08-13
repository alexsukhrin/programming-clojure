(ns examples.sequences)

;; Sequences

;; Everything Is a Sequence
(def aseq [1 2 3 4 5])

(first aseq) ;; 1

(rest aseq) ;; (2 3 4 5)

;; construct a new sequence by adding an item to the front of an existing sequence
(cons 2 aseq) ;; (2 1 2 3 4 5)

(seq aseq) ;; (1 2 3 4 5)

;; (next aseq) is equivalent to (seq (rest aseq))
(next aseq) ;; (2 3 4 5)

(seq? (rest [1 2 3])) ;; true

(first {:fname "Aaron" :lname "Bedra"}) ;; [:fname "Aaron"]

(rest {:fname "Aaron" :lname "Bedra"}) ;; ([:lname "Bedra"])

(cons [:mname "James"] {:fname "Aaron" :lname "Bedra"}) ;; ([:mname "James"] [:fname "Aaron"] [:lname "Bedra"])

(first #{:the :quick :brown :fox}) ;; :fox

(rest #{:the :quick :brown :fox}) ;; (:the :quick :brown)

(cons :jumped #{:the :quick :brown :fox}) ;; (:jumped :fox :the :quick :brown)

#{:the :quick :brown :fox} ;; #{:fox :the :quick :brown}

;; (sorted-set & elements)
(sorted-set :the :quick :brown :fox) ;; #{:brown :fox :quick :the}

;; (sorted-map & elements)
(sorted-map :c 3 :b 2 :a 1) ;; {:a 1, :b 2, :c 3}

;; (conj coll element & elements)
;; (into to-coll from-coll)
(conj '(1 2 3) :a) ;; (:a 1 2 3)

(into '(1 2 3) '(:a :b :c)) ;; (:c :b :a 1 2 3)

(conj [1 2 3] :a) ;; [1 2 3 :a]

(into [1 2 3] [:a :b :c]) ;; [1 2 3 :a :b :c]

(list? (rest [1 2 3])) ;; false

(seq? (rest [1 2 3])) ;; true

;; Creating Sequences
;; (range start? end? step?)

;; end only
(range 10) ;; (0 1 2 3 4 5 6 7 8 9)

;; start + end
(range 10 20) ;; (10 11 12 13 14 15 16 17 18 19)

;; step by 2
(range 1 25 2) ;; (1 3 5 7 9 11 13 15 17 19 21 23)

;; negative step
(range 0 -1 -0.25) ;; (0 -0.25 -0.5 -0.75)

;; ratios
(range 1/2 4 1) ;; (1/2 3/2 5/2 7/2)

(repeat 5 1) ;; (1 1 1 1 1)

(repeat 10 "x") ;; ("x" "x" "x" "x" "x" "x" "x" "x" "x" "x")

;; (iterate f x)
(take 10 (iterate inc 1)) ;; (1 2 3 4 5 6 7 8 9 10)

;; (take n sequence)
(def whole-numbers (iterate inc 1))

;; (repeat x)
(take 20 (repeat 1)) ;; (1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1)

;; (cycle coll)
(take 10 (cycle (range 3))) ;; (0 1 2 0 1 2 0 1 2 0)

;; (interleave & colls)
(interleave whole-numbers ["A" "B" "C" "D" "E"]) ;; (1 "A" 2 "B" 3 "C" 4 "D" 5 "E")

;; (interpose separator coll)
(interpose "," ["apples" "bananas" "grapes"]) ;; ("apples" "," "bananas" "," "grapes")

(apply str (interpose "," ["apples" "bananas" "grapes"])) ;; "apples,bananas,grapes"

;; (join separator sequence)
(require '[clojure.string :refer [join]])

(join \, ["apples" "bananas" "grapes"]) ;; "apples,bananas,grapes"

(set [1 2 3]) ;; #{1 3 2}

(hash-set 1 2 3) ;; #{1 3 2}

(vec (range 3)) ;; [0 1 2]


;; Filtering Sequences

;; (filter pred coll)
(take 10 (filter even? whole-numbers)) ;; (2 4 6 8 10 12 14 16 18 20)

(take 10 (filter odd? whole-numbers)) ;; (1 3 5 7 9 11 13 15 17 19)

;; (take-while pred coll)
(def vowel? #{\a \e \i \o \u})
(def consonant? (complement vowel?))

(take-while consonant? "the-quick-brown-fox") ;; (\t \h)

;; (drop-while pred coll)
(drop-while consonant? "the-quick-brown-fox") ;; (\e \- \q \u \i \c \k \- \b \r \o \w \n \- \f \o \x)

(split-at 5 (range 10)) ;; [(0 1 2 3 4) (5 6 7 8 9)]

(split-with #(<= % 10) (range 0 20 2)) ;; [(0 2 4 6 8 10) (12 14 16 18)]

;; Sequence Predicates
(every? odd? [1 3 5]) ;; true

(every? odd? [1 3 5 8]) ;; false

;; (some pred coll)
(some even? [1 2 3]) ;; true

(some even? [1 3 5]) ;; nil

(some identity [nil false 1 nil 2]) ;; 1

(some #{3} (range 20)) ;; 3

(not-every? even? whole-numbers) ;; true

(not-any? even? whole-numbers) ;; false

;; Transforming Sequences

;; (map f coll)
(map #(format "<p>%s</p>" %) ["the" "quick" "brown" "fox"]) ;; ("<p>the</p>" "<p>quick</p>" "<p>brown</p>" "<p>fox</p>")

(map #(format "<%s>%s</%s>" %1 %2 %1)
     ["h1" "h2" "h3" "h1"] 
     ["the" "quick" "brown" "fox"]) ;; ("<h1>the</h1>" "<h2>quick</h2>" "<h3>brown</h3>" "<h1>fox</h1>")

;; (reduce f coll)
(reduce + (range 1 11)) ;; 55

(reduce * (range 1 11)) ;; 3628800

;; (sort comp? coll)
(sort [42 1 7 11]) ;; (1 7 11 42)

(sort-by #(.toString %) [42 1 7 11]) ;; (1 11 42 7)

(sort > [42 1 7 11]) ;; (42 11 7 1)

(sort-by :grade > [{:grade 83} {:grade 90} {:grade 77}]) ;; ({:grade 90} {:grade 83} {:grade 77})

;; (for [binding-form coll-expr filter-expr? ...] expr)
(for [word ["the" "quick" "brown" "fox"]] 
  (format "<p>%s</p>" word)) ;; ("<p>the</p>" "<p>quick</p>" "<p>brown</p>" "<p>fox</p>")

(take 10 (for [n whole-numbers :when (even? n)] n)) ;; (2 4 6 8 10 12 14 16 18 20)

(for [n whole-numbers :while (even? n)] n) ;; ()

(for [file "ABCDEFGH" rank (range 1 9)]
  (format "%c%d" file rank)) ;; ("A1" "A2" ... elided ... "H7 ""H8")

(for [rank (range 1 9) file "ABCDEFGH"]
  (format "%c%d" file rank)) ;; ("A1" "B1" ... elided ... "G8" "H8")


;; Lazy and Infinite Sequences

;; Taken from clojure.contrib.lazy-seqs
;; primes cannot be written efficiently as a function, because
;; it needs to look back on the whole sequence. contrast with
;; fibs and powers-of-2 which only need a fixed buffer of 1 or 2 ; previous values.
(def primes
  (concat
   [2 3 5 7]
   (lazy-seq
(let [primes-from
(fn primes-from [n [f & r]]
(if (some #(zero? (rem n %))
(take-while #(<= (* % %) n) primes))
              (recur (+ n f) r)
(lazy-seq (cons n (primes-from (+ n f) r))))) wheel(cycle[24246264246626 4 2 64684242486462 4 6
                        2 6 6 4 2 4 6 2 6 4 2 4 2 10 2 10])]
      (primes-from 11 wheel)))))

(def ordinals-and-primes 
  (map vector (iterate inc 1) primes))

(take 5 (drop 1000 ordinals-and-primes))

(def x (for [i (range 1 3)] (do (println i) i)))

;; (doall coll)
(doall x) ;; (1 2)

;; (dorun coll)
(dorun x)

;; String.getBytes returns a byte array
(first (.getBytes "hello")) ;; 104

(rest (.getBytes "hello")) ;; (101 108 108 111)

(cons (int \h) (.getBytes "ello")) ;; (104 101 108 108 111)

;; System.getProperties returns a Hashtable
(first (System/getProperties))

(rest (System/getProperties))

(first "Hello") ;; \H

(rest "Hello") ;; (\e \l \l \o)

(cons \H "ello") ;; (\H \e \l \l \o)

(reverse "hello") ;; (\o \l \l \e \h)

(apply str (reverse "hello")) ;; "olleh"


;; Seq-ing Regular Expressions

;; (re-matcher regexp string)
(let [m (re-matcher #"\w+" "the quick brown fox")]
  (loop [match (re-find m)] 
    (when match 
      (println match) 
      (recur (re-find m)))))

;; (re-seq regexp string)
(re-seq #"\w+" "the quick brown fox") ;; ("the" "quick" "brown" "fox")

(sort (re-seq #"\w+" "the quick brown fox")) ;; ("brown" "fox" "quick" "the"

(drop 2 (re-seq #"\w+" "the quick brown fox")) ;; ("brown" "fox")

(map clojure.string/upper-case (re-seq #"\w+" "the quick brown fox")) ;; ("THE" "QUICK" "BROWN" "FOX")


;; Seq-ing the File System
(import 'java.io.File)

(.listFiles (File. "."))

(seq (.listFiles (File. ".")))

;; overkill
(map #(.getName %) (seq (.listFiles (File. ".")))) ;; ("README.md" "examples" ".git")

(map #(.getName %) (.listFiles (File. "."))) ;; ("README.md" "examples" ".git")

(count (file-seq (File. ".")))

(defn minutes-to-millis [mins] (* mins 1000 60))

(defn recently-modified? [file]
  (> (.lastModified file) 
     (- (System/currentTimeMillis) (minutes-to-millis 30))))

(filter recently-modified? (file-seq (File. ".")))


;; Seq-ing a Stream

(require '[clojure.java.io :refer [reader]])

(take 2 (line-seq (reader "src/examples/sequences.clj")))

(with-open [rdr (reader "src/examples/sequences.clj")] 
  (count (line-seq rdr)))

(use '[clojure.java.io :only (reader)])
(use '[clojure.string :only (blank?)])

(defn non-blank? [line] (not (blank? line)))
(defn non-svn? [file] (not (.contains (.toString file) ".svn")))
(defn clojure-source? [file] (.endsWith (.toString file) ".clj"))

(defn clojure-loc [base-file] 
  (reduce 
   + 
   (for [file (file-seq base-file) 
         :when (and (clojure-source? file) (non-svn? file))] 
     (with-open [rdr (reader file)] 
       (count (filter non-blank? (line-seq rdr)))))))

(clojure-loc (java.io.File. "/home/abedra/src/opensource/clojure/clojure"))

;; Calling Structure-Specific Functions
(peek '(1 2 3)) ;;  1

(pop '(1 2 3)) ;; (2 3)

(rest ()) ;; ()

(pop ()) ;; java.lang.IllegalStateException: Can't pop empty list


;; Functions on Vectors

;; Vectors also support peek and pop
(peek [1 2 3]) ;; 3

(pop [1 2 3]) ;; [1 2]

(get [:a :b :c] 1) ;; :b

(get [:a :b :c] 5)

([:a :b :c] 1) ;; :b

([:a :b :c] 5) ;; java.lang.IndexOutOfBoundsException

(assoc [0 1 2 3 4] 2 :two) ;; [0 1 :two 3 4]

(subvec [1 2 3 4 5] 3) ;; [4 5]

(subvec [1 2 3 4 5] 1 3) ;; [2 3]

(take 2 (drop 1 [1 2 3 4 5])) ;; (2 3)


;; Functions on Maps

;; (keys map)
;; (vals map)

(keys {:sundance "spaniel", :darwin "beagle"}) ;; (:sundance :darwin)

(vals {:sundance "spaniel", :darwin "beagle"}) ;; ("spaniel" "beagle")

;; (get map key value-if-not-found?)

(get {:sundance "spaniel", :darwin "beagle"} :darwin) ;; "beagle"

(get {:sundance "spaniel", :darwin "beagle"} :snoopy) ;; nil

({:sundance "spaniel", :darwin "beagle"} :darwin) ;; "beagle"

({:sundance "spaniel", :darwin "beagle"} :snoopy) ;; nil

;; Keywords are also functions.
(:darwin {:sundance "spaniel", :darwin "beagle"}) ;; "beagle"

(:snoopy {:sundance "spaniel", :darwin "beagle"}) ;; nil

;; (contains? map key)
(def score {:stu nil :joey 100})

(:stu score)

(contains? score :stu) ;; true

(get score :stu :score-not-found) ;; nil

(get score :aaron :score-not-found) ;; :score-not-found

;; Clojure also provides several functions for building new maps:
;; • assoc returns a map with a key/value pair added.
;; • dissoc returns a map with a key removed.
;; • select-keys returns a map, keeping only a specified set of keys.
;; • merge combines maps. If multiple maps contain a key, the rightmost wins.

(def song {:name "Agnus Dei"
           :artist "Krzysztof Penderecki"
           :album "Polish Requiem" :genre "Classical"})

(assoc song :kind "MPEG Audio File") ;; {:name "Agnus Dei", :album "Polish Requiem", :kind "MPEG Audio File", :genre "Classical", :artist "Krzysztof Penderecki"}

(dissoc song :genre) ;; {:name "Agnus Dei", :album "Polish Requiem", :artist "Krzysztof Penderecki"}

(select-keys song [:name :artist]) ;; {:name "Agnus Dei", :artist "Krzysztof Penderecki"}

(merge song {:size 8118166, :time 507245}) ;; {:name "Agnus Dei", :album "Polish Requiem", :genre "Classical", :size 8118166, :artist "Krzysztof Penderecki", :time 507245}

;; (merge-with merge-fn & maps)
(merge-with
 concat
 {:rubble ["Barney"], :flintstone ["Fred"]} 
 {:rubble ["Betty"], :flintstone ["Wilma"]} 
 {:rubble ["Bam-Bam"], :flintstone ["Pebbles"]}) ;; {:rubble ("Barney" "Betty" "Bam-Bam"), :flintstone ("Fred" "Wilma" "Pebbles")}


;; Functions on Sets

(def languages #{"java" "c" "d" "clojure"})
(def beverages #{"java" "chai" "pop"})

;; The first group of clojure.set functions performs operations from set theory:
;; • union returns the set of all elements present in either input set.
;; • intersection returns the set of all elements present in both input sets.
;; • difference returns the set of all elements present in the first input set, minus those in the second.
;; • select returns the set of all elements matching a predicate.
(require '[clojure.set :as set])

(set/union languages beverages) ;; #{"d" "clojure" "pop" "java" "chai" "c"}

(set/difference languages beverages) ;; #{"d" "clojure" "c"}

(set/intersection languages beverages) ;; #{"java"}

(set/select #(= 1 (count %)) languages) ;; #{"d" "c"}

(def compositions
  #{{:name "The Art of the Fugue" :composer "J. S. Bach"}
    {:name "Musical Offering" :composer "J. S. Bach"}
    {:name "Requiem" :composer "Giuseppe Verdi"} 
    {:name "Requiem" :composer "W. A. Mozart"}})

(def composers
  #{{:composer "J. S. Bach" :country "Germany"}
    {:composer "W. A. Mozart" :country "Austria"}
    {:composer "Giuseppe Verdi" :country "Italy"}}) 

(def nations 
  #{{:nation "Germany" :language "German"} 
    {:nation "Austria" :language "German"}
    {:nation "Italy" :language "Italian"}})

;; (rename relation rename-map)
(set/rename compositions {:name :title}) ;; #{{:title "Requiem", :composer "Giuseppe Verdi"}
                                         ;;   {:title "Musical Offering", :composer "J.S. Bach"} {:title "Requiem", :composer "W. A. Mozart"}
                                         ;;   {:title "The Art of the Fugue", :composer "J.S. Bach"}}

;; (select pred relation)
(set/select #(= (:name %) "Requiem") compositions) ;; #{{:name "Requiem", :composer "Giuseppe Verdi"} {:name "Requiem", :composer "W. A. Mozart"}}

;; (project relation keys)
(set/project compositions [:name]) ;; #{{:name "The Art of the Fugue"} {:name "Musical Offering"} {:name "Requiem"}}

(for [m compositions c composers] (concat m c)) ;; (([:name "Musical Offering"] [:composer "J. S. Bach"] [:composer "Giuseppe Verdi"] [:country "Italy"]) ([:name "Musical Offering"] [:composer "J. S. Bach"] [:composer "J. S. Bach"] [:country "Germany"]) ([:name "Musical Offering"] [:composer "J. S. Bach"] [:composer "W. A. Mozart"] [:country "Austria"]) ([:name "The Art of the Fugue"] [:composer "J. S. Bach"] [:composer "Giuseppe Verdi"] [:country "Italy"]) ([:name "The Art of the Fugue"] [:composer "J. S. Bach"] [:composer "J. S. Bach"] [:country "Germany"]) ([:name "The Art of the Fugue"] [:composer "J. S. Bach"] [:composer "W. A. Mozart"] [:country "Austria"]) ([:name "Requiem"] [:composer "Giuseppe Verdi"] [:composer "Giuseppe Verdi"] [:country "Italy"]) ([:name "Requiem"] [:composer "Giuseppe Verdi"] [:composer "J. S. Bach"] [:country "Germany"]) ([:name "Requiem"] [:composer "Giuseppe Verdi"] [:composer "W. A. Mozart"] [:country "Austria"]) ([:name "Requiem"] [:composer "W. A. Mozart"] [:composer "Giuseppe Verdi"] [:country "Italy"]) ([:name "Requiem"] [:composer "W. A. Mozart"] [:composer "J. S. Bach"] [:country "Germany"]) ([:name "Requiem"] [:composer "W. A. Mozart"] [:composer "W. A. Mozart"] [:country "Austria"]))

;; (join relation-1 relation-2 keymap?)
(set/join compositions composers) ;; #{{:composer "W. A. Mozart", :country "Austria", :name "Requiem"} {:composer "J. S. Bach", :country "Germany", :name "Musical Offering"} {:composer "Giuseppe Verdi", :country "Italy", :name "Requiem"} {:composer "J. S. Bach", :country "Germany", :name "The Art of the Fugue"}}

(set/join composers nations {:country :nation}) ;; #{{:composer "W. A. Mozart", :country "Austria", :nation "Austria", :language "German"} {:composer "J. S. Bach", :country "Germany", :nation "Germany", :language "German"} {:composer "Giuseppe Verdi", :country "Italy", :nation "Italy", :language "Italian"}}

(set/project
 (set/join
  (set/select #(= (:name %) "Requiem") compositions) composers)
 [:country]) ;; #{{:country "Italy"} {:country "Austria"}}

;; Wrapping Up

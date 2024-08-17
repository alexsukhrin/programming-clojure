(ns examples.spec)

(require '[clojure.spec.alpha :as s])

;; Specs describing an ingredient
(s/def ::ingredient (s/keys :req [::name ::quantity ::unit]))
(s/def ::name string?)
(s/def ::quantity number?)
(s/def ::unit keyword?)

;; Function spec for scale-ingredient
(s/fdef scale-ingredient
  :args (s/cat :ingredient ::ingredient :factor number?)
  :ret  ::ingredient)

(defn scale-ingredient [ingredient factor]
  (update ingredient :quantity * factor))

;; Validating Data

;; Predicates
(s/def :my.app/company-name string?)

(s/valid? :my.app/company-name "Acme Moving") ;; true

(s/valid? :my.app/company-name 100) ;; false

;; Enumerated values
(s/def :marble/color #{:red :green :blue})

(s/valid? :marble/color :red) ;; true

(s/valid? :marble/color :pink) ;; false

(s/def :bowling/roll #{0 1 2 3 4 5 6 7 8 9 10})

(s/valid? :bowling/roll 5) ;; true

;; Range Specs
(s/def :bowling/ranged-roll (s/int-in 0 11))

(s/valid? :bowling/ranged-roll 10) ;; true

;; Handling nil
(s/def :my.app/company-name-2 (s/nilable string?))

(s/valid? :my.app/company-name-2 nil) ;; true

(s/def ::nilable-boolean (s/nilable boolean?))

;; Logical Specs
(s/def ::odd-int (s/and int? odd?))

(s/valid? ::odd-int 5) ;; true

(s/valid? ::odd-int 10) ;; false

(s/valid? ::odd-int 5.2) ;; false

(s/def ::odd-or-42 (s/or :odd ::odd-int :42 #{42}))

(s/conform ::odd-or-42 42) ;; [:42 42]

(s/conform ::odd-or-42 19) ;; [:odd 19]

(s/explain ::odd-or-42 0) ;; 0 - failed: odd? at: [:odd] spec: :examples.spec/odd-int
                          ;; 0 - failed: #{42} at: [:42] spec: :examples.spec/odd-or-42

;; Collection Specs
(s/def ::names (s/coll-of string?))
(s/valid? ::names ["Alex" "Stu"]) ;; true

(s/valid? ::names #{"Alex" "Stu"}) ;; true

(s/valid? ::names '("Alex" "Stu")) ;; true

(s/def ::my-set (s/coll-of int? :kind set? :min-count 2))

(s/valid? ::my-set #{10 20}) ;; true

(s/def ::scores (s/map-of string? int?))

(s/valid? ::scores {"Stu" 100, "Alex" 200}) ;; true

;; Collection Sampling

;; Tuples
(s/def ::point (s/tuple float? float?))

(s/conform ::point [1.3 2.7]) ;; [1.3 2.7]

;; Information Maps
{:music/id #uuid "40e30dc1-55ac-33e1-85d3-1f1508140bfc"
 :music/artist "Rush"
 :music/title "Moving Pictures"
 :music/date #inst "1981-02-12"}

(s/def :music/id uuid?)
(s/def :music/artist string?)
(s/def :music/title string?)
(s/def :music/date inst?)

(s/def :music/release
  (s/keys :req [:music/id]
          :opt [:music/artist
                :music/title
                :music/date]))

;; Validating Functions

;; Sequences With Structure
(s/def ::cat-example (s/cat :s string? :i int?))

(s/valid? ::cat-example ["abc" 100]) ;; true

(s/conform ::cat-example ["abc" 100]) ;; {:s "abc", :i 100}

(s/def ::alt-example (s/alt :i int? :k keyword?))

(s/valid? ::alt-example [100]) ;; true

(s/valid? ::alt-example [:foo]) ;; true

(s/conform ::alt-example [:foo]) ;; [:k :foo]

(s/def ::oe (s/cat :odds (s/+ odd?) :even (s/? even?)))

(s/conform ::oe [1 3 5 100]) ;; {:odds [1 3 5], :even 100}

(s/def ::odds (s/+ odd?))

(s/def ::optional-even (s/? even?))

(s/def ::oe2 (s/cat :odds ::odds :even ::optional-even))

(s/conform ::oe2 [1 3 5 100]) ;; {:odds [1 3 5], :even 100}

;; Variable Argument Lists
(s/def ::println-args (s/* any?))

(s/def ::intersection-args
  (s/cat :s1 set?
         :sets (s/* set?)))

(s/conform ::intersection-args '[#{1 2} #{2 3} #{2 5}])

(s/def ::intersection-args-2 (s/+ set?))

(s/conform ::intersection-args-2 '[#{1 2} #{2 3} #{2 5}]) ;; [#{1 2} #{3 2} #{2 5}]

(s/def ::meta map?)

(s/def ::validator ifn?)

(s/def ::atom-args
  (s/cat :x any? :options (s/keys* :opt-un [::meta ::validator])))

(s/conform ::atom-args [100 :meta {:foo 1} :validator int?]) ;; {:x 100, :options {:meta {:foo 1}, :validator #object[clojure.core$int_QMARK_ 0x58a340e9 "clojure.core$int_QMARK_@58a340e9"]}}

;; Multi-arity Argument Lists
(s/def ::repeat-args
  (s/cat :n (s/? int?) :x any?))

(s/conform ::repeat-args [100 "foo"]) ;; {:n 100, :x "foo"}

(s/conform ::repeat-args ["foo"]) ;; {:x "foo"}

;; Specifying Functions
(s/def ::rand-args (s/cat :n (s/? number?)))

(s/def ::rand-ret double?)

(s/def ::rand-fn
  (fn [{:keys [args ret]}]
    (let [n (or (:n args) 1)]
      (cond (zero? n) (zero? ret)
            (pos? n) (and (>= ret 0) (< ret n))
            (neg? n) (and (<= ret 0) (> ret n))))))

(s/fdef clojure.core/rand
  :args ::rand-args
  :ret  ::rand-ret
  :fn   ::rand-fn)

;; Anonymous Functions
(defn opposite [pred]
  (comp not pred))

(s/def ::pred
  (s/fspec :args (s/cat :x any?)
           :ret  boolean?))

(s/fdef opposite
  :args (s/cat :pred ::pred)
  :ret ::pred)

;; Instrumenting Functions
(require '[clojure.spec.test.alpha :as stest])

(stest/instrument 'clojure.core/rand)

(stest/instrument (stest/enumerate-namespace 'clojure.core))

;; Generative Function Testing

;; Checking Functions
(s/fdef clojure.core/symbol
  :args (s/cat :ns (s/? string?) :name string?) :ret symbol?
  :fn (fn [{:keys [args ret]}]
        (and (= (name ret) (:name args))
             (= (namespace ret) (:ns args)))))

(stest/check 'clojure.core/symbol) ;; ({:sym clojure.core/symbol
                                   ;;  :spec #object[clojure.spec.alpha$fspec_impl$reify__14282 ...],
                                   ;;  :clojure.spec.test.check/ret {
                                   ;;        :result true,
                                   ;;        :num-tests 1000,
                                   ;;        :seed 1485241441400}})

;; Generating Examples
(s/exercise (s/cat :ns (s/? string?) :name string?))

(def x 101)

;; Combining Generators With s/and
(defn big? [] (> x 100))

(s/def ::big-odd (s/and odd? big?))

(s/exercise ::big-odd)

(s/def :marble/color-red
  (s/with-gen :marble/color #(s/gen #{:red})))

(s/exercise :marble/color-red)

(require '[clojure.string :as str])

(s/def :sku
  (s/with-gen (s/and string? #(str/starts-with? % "SKU-"))
    (fn [] (gen/fmap #(str "SKU-" %) (s/gen string?)))))

;; Wrapping Up

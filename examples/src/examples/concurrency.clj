(ns examples.concurrency)

;; Refs and Software Transactional Memory

;; (ref initial-state)
(def current-track (ref "Mars, the Bringer of War"))

;; (deref reference)
(deref current-track) ;; "Mars, the Bringer of War"

;; equal
@current-track ;; "Mars, the Bringer of War"

;; ref-set
;; (ref-set reference new-value)
(ref-set current-track "Venus, the Bringer of Peace") ;; java.lang.IllegalStateException: No transaction running

;; (dosync & exprs)
(dosync (ref-set current-track "Venus, the Bringer of Peace")) ;; "Venus, the Bringer of Peace"

;; Transactional Properties

;; Software Transactional Memory (STM)

;; Updates are atomic. If you update more than one ref in a transaction, the cumulative effect of all the updates will appear as a single instantaneous event to anyone not inside your transaction.
;; • Updates are consistent. Refs can specify validation functions. If any of these functions fail, the entire transaction will fail.
;; • Updates are isolated. Running transactions can’t see partially completed results from other transactions.
(def current-track (ref "Venus, the Bringer of Peace"))
(def current-composer (ref "Holst"))

(dosync
 (ref-set current-track "Credo") 
 (ref-set current-composer "Byrd"))

;; alter
(defrecord Message [sender text])

(->Message "Aaron" "Hello")

(def messages (ref ()))

;; bad idea
(defn naive-add-message [msg]
  (dosync (ref-set messages (cons msg @messages))))

;; (alter ref update-fn & args...)
(defn add-message [msg]
  (dosync (alter messages conj msg)))

(add-message (->Message "user 1" "hello")) ;; (#examples.concurrency.Message{:sender "user 1", :text "hello"})

(add-message (->Message "user 2" "howdy")) ;; (#examples.concurrency.Message{:sender "user 2", :text "howdy"} #examples.concurrency.Message{:sender "user 1", :text "hello"})

;; How STM Works: MVCC

;; commute
;; (commute ref update-fn & args...)
(defn add-message-commute [msg]
  (dosync (commute messages conj msg)))

(add-message-commute (->Message "user 4" "howdy added ++"))

;; Adding Validation to Refs
(defn valid-message? [msg]
  (and (:sender msg) (:text msg)))

(def validate-message-list 
  #(every? valid-message? %)) 

(def messages (ref () :validator validate-message-list))

(add-message "not a valid message") ;; java.lang.IllegalStateException: Invalid reference state

@messages ;; ()

(add-message (->Message "Aaron" "Real Message")) ;; (#examples.concurrency.Message{:sender "Aaron", :text "Real Message"})

;; Use Atoms for Uncoordinated, Synchronous Updates

;; (atom initial-state options?)
(def current-track (atom "Venus, the Bringer of Peace"))

(deref current-track) ;; "Venus, the Bringer of Peace"

@current-track ;; "Venus, the Bringer of Peace"

(reset! current-track "Credo") ;; "Credo"

(def current-track (atom {:title "Credo" :composer "Byrd"}))

(reset! current-track {:title "Spem in Alium" :composer "Tallis"})

(swap! current-track assoc :title "Sancte Deus") ;; {:title "Sancte Deus", :composer "Tallis"}

;; Use Agents for Asynchronous Updates

;; (agent initial-state)
(def counter (agent 0))

;; (send agent update-fn & args)
(send counter inc)

@counter

;; Validating Agents and Handling Errors
(def counter (agent 0 :validator number?))

(send counter (fn [_] "boo"))

(agent-error counter)

(restart-agent counter 0) ;; 0

@counter ;; 0

(defn handler [agent err]
  (println "ERR!" (.getMessage err)))

(def counter2 (agent 0 :validator number? :error-handler handler))

(send counter2 (fn [_] "boo"))

(send counter2 inc)

@counter2 ;; 1

;; Including Agents in Transactions
(def backup-agent (agent "output/messages-backup.clj"))

(defn add-message-with-backup [msg] 
  (dosync 
   (let [snapshot (commute messages conj msg)] 
     (send-off backup-agent (fn [filename] 
                              (spit filename snapshot) 
                              filename)) 
     snapshot)))

(add-message-with-backup (->Message "John" "Message One"))

(add-message-with-backup (->Message "Jane" "Message Two"))

;; The Unified Update Model

;; Managing Per-Thread State with Vars
(def ^:dynamic foo 10)

foo ;; 10

(.start (Thread. (fn [] (println foo)))) ;; nil
                                         ;; 10

;; (binding [bindings] & body)
(binding [foo 42] foo) ;; 42

(defn print-foo [] (println foo))

(let [foo "let foo"] (print-foo)) ;; 10

(binding [foo "bound foo"] (print-foo)) ;; bound foo

;; Acting at a Distance

(defn ^:dynamic slow-double [n] 
  (Thread/sleep 100)
  (* n 2))

(defn calls-slow-double []
  (map slow-double [1 2 1 2 1 2]))

(time (dorun (calls-slow-double))) ;; "Elapsed time: 622.86675 msecs"

;; (memoize function)
(defn demo-memoize [] 
  (time 
   (dorun 
    (binding [slow-double (memoize slow-double)] 
      (calls-slow-double)))))

(demo-memoize) ;; "Elapsed time: 210.404166 msecs"

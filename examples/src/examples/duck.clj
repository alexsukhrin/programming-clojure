(ns examples.duck)

(defprotocol Swimmable
  (swim [this] "Suitable for swimming"))

(defprotocol Messenger
  (sendMessage [this])
  (getMessage [this]))

(defrecord Duck [name]
         Swimmable
  (swim [this]
        "Duck swim!" name))

(def duck (->Duck "Katya"))
(.swim duck)

(defrecord Telegram [] 
  Messenger 
  (sendMessage [this] "Send telegram!") 
  (getMessage [this] "Read message telegram!"))

(defrecord Viber []
  Messenger
  (sendMessage [this] "Send Viber")
  (getMessage [this] "Read message Viber"))

(def viber (Viber.))
(.getMessage viber)

(def telegram1 (Telegram.))
(.sendMessage telegram1)
(.getMessage telegram1)

(extend-type Telegram
  Swimmable
  (swim [this] 
    "Telegram, swim!"))

(swim telegram1)

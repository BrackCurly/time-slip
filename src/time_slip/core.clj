(ns time-slip.core
  (:require [clj-http.client :as client]
            [clj-time.core :as t]
            [clj-time.predicates :as pr]
            [clj-time.coerce :as coerce]
            [time-slip.text :as txt]
            [time-slip.feed :as f]))

(def feeds ["http://www.tagesschau.de/xml/rss2"
            "http://www.faz.net/rss/aktuell/"
            "http://newsfeed.zeit.de/news/index"])

(def today  (t/local-date 2014 1 1) #_(t/today-at 0 0))
(def today-timestamp (coerce/to-long start-date))

(defn parse-res [res]
  (-> res :body f/parse))

(defn nouns-freq [s]
  (->> s
       txt/get-words
       txt/noun-sub-seqs
       frequencies))

(defn noun-seq-freq [[s n] nouns-freq]
  (->> (drop 1 nouns-freq)
       (take-while (fn [[_ n]] (> n 1)))
       (filter (fn[[s-seq _]] (.contains s-seq s)))))

(defn fetch-most-freq-noun []
  (let [nouns-freq (->> feeds
                        (map client/get)
                        (filter #(= (:status %) 200))
                        (mapcat parse-res)
                        (filter #(>= (:timestamp %) today-timestamp))
                        (map :title)
                        (map nouns-freq)
                        (apply merge-with +)
                        (sort-by second)
                        reverse)
        noun (first nouns-freq)
        noun-seq (first (noun-seq-freq noun nouns-freq))]
    (if (nil? noun)
      ""
      (if (nil? noun-seq)
      (first noun)
      (if (> (/ (second noun) 2) (second noun-seq))
        (first noun)
        (first noun-seq))))))

(defn month-str [month]
  (str "    " (case month
                1 "JANUAR"
                2 "FEBRUAR"
                3 "MÄRZ"
                4 "APRIL"
                5 "MAI"
                6 "JUNI"
                7 "JULI"
                8 "AUGUST"
                9 "SEPTEMBER"
                10 "OKOTBER"
                11 "NOVEMBER"
                12 "DEZEMBER") "\n\n"))

(defn day-str [day word]
  (str (if (< day 10) " " "")
       day ". " (subs word 0 (min 20 (count word)))))

(defn first-day-of-year [date]
  (and (= (t/day date) 1)
       (= (t/month date) 1)))

(defn main []
  (if (pr/first-day-of-month? today)
    (println (month-str (t/month today))))
  (println (day-str (t/day today) (fetch-most-freq-noun))))

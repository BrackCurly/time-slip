(ns time-slip.feed-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as time]
            [time-slip.feed :refer :all]))

(deftest parse-test
  (testing "parse xml file"
    (let [items (-> "test/rss2sample.xml" slurp parse)]
      (is (= (count items) 4))
      (is (= (-> items first :text) "Star City How do Americans get ready to work with Russians aboard the International Space Station? They take a crash course in culture, language and protocol at Russia's Star City."))
      (is (= (-> items first :date) (time/date-time 2003 6 3 8 39 21))))))

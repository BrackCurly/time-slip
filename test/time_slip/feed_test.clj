(ns time-slip.feed-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as time]
            [time-slip.feed :refer :all]))

(deftest parse-date-test
  (testing "support GMT as timzone"
    (is (= (parse-date "Fri, 28 Mar 2014 13:03:10 GMT")
           (time/date-time 2014 3 28 12 3 10)))))

(deftest parse-test
  (testing "parse xml file"
    (let [items (-> "test/rss2sample.xml" slurp parse)]
      (is (= (count items) 4))
      (is (= (-> items first :title) "Star City"))
      (is (= (-> items first :date) (time/date-time 2003 6 3 8 39 21))))))

(ns time-slip.feed-test
  (:require [clojure.test :refer :all]
            [time-slip.feed :refer :all]))

(deftest parse-rss
  (testing "parse xml file"
    (let [items (-> "test/rss2sample.xml" slurp parse)]
      (is (= (count items) 4)))))

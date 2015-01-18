(ns time-slip.feed-test
  (:require [clojure.test :refer :all]
            [time-slip.feed :refer :all]))

(deftest parse-to-timestamp-test
  (testing "support GMT as timzone"
    (is (= (parse-to-timestamp "Fri, 28 Mar 2014 13:03:10 GMT")
            1396008190000))))

(deftest parse-test
  (testing "parse xml file"
    (let [items (-> "test/rss2sample.xml" slurp parse)]
      (is (= (count items) 4))
      (is (= (-> items first :title) "Star City"))
      (is (= (-> items first :timestamp) 1054629561000)))))

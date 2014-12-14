(ns lab2.crawler-test
  (:require [clojure.test :refer :all]
            [lab2.crawler :refer :all]))

(deftest test-parser-all-urls
  (let [body "<body><a href=\"https://google.com/some-failed-page.html\">link-here</a>
    <h1>Header</h1><a href=\"http://onliner.by/some-failed-page.html\">link</a>
    <br><a href=\"/news.html\">link</a>"]

    (is (=
      (parser-all-urls body)
      '("https://google.com/some-failed-page.html" "http://onliner.by/some-failed-page.html")))))

(deftest test-goto-url
  (testing "404"
    (with-redefs [get-result (fn [_] {:status 404 :body ""})]
      (is (=
        (goto-url "url" 1 1 (atom #{}))
        404)))))

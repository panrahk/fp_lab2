(ns lab2.crawler
  (:gen-class)
  (:require [org.httpkit.client :as http]))

(defn get-result
  [url]
  @(http/get url {:follow-redirects false :throw-exceptions false}))


(defn parser-all-urls
  [body]
  (map #(% 1) (re-seq #"href=[\'\"]?((http|https)\:\/\/[^\'\" >]+)" body))) ; "


(defn show-current-level
  [level & args]
  (apply println (apply str (repeat (* (- level 1) 4) " ")) args))



(defn goto-url

  [url current-level max-level-deep checked-urls]
  (cond
    (and (<= current-level max-level-deep) (not (contains? @checked-urls url)))
    (do

      (swap! checked-urls #(conj % url))
      (let [response (get-result url)
            status (:status response)
            body (:body response)]

        (cond
          (= status 200)
          (let [urls (parser-all-urls body)]
              (show-current-level current-level url (count urls) "links")
              (doall (pmap #(goto-url % (+ current-level 1) max-level-deep checked-urls) urls)))

          (contains? #{301 302 307} status)

          (let [redirect-url (:location (:headers response))]
            (show-current-level current-level url "redirect" redirect-url)
            (goto-url redirect-url (+ current-level 1) max-level-deep checked-urls))

          :else

          (do
              (show-current-level current-level url "bad")
             status))))))

(defn -main
  [url depth]
  (goto-url url 1 (Integer/parseInt depth) (atom #{}))
  (shutdown-agents))

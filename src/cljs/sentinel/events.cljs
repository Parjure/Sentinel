(ns sentinel.events
  (:require [re-frame.core :as re-frame]
            [sentinel.db :as db]
            [cljs.reader :as reader]
            [ajax.core :refer [GET]]))


(re-frame/reg-event-db
  :server-name-changed
  (fn [db [_ name]]
    (assoc db :server-name name)))

(re-frame/reg-event-db
  :server-url-changed
  (fn [db [_ url]]
    (assoc db :server-url url)))

(re-frame/reg-event-db
  :add-server
  (fn [db]
    (println "in :add-server")
    (update db :servers conj [(:server-name db) (:server-url db)])))

(re-frame/reg-event-db
  :initialize-db
  (fn [_]
    db/default-db))

(re-frame/reg-event-db
  :server-status-success
  (fn [db [_ [server-name status]]]
    (println "in :server-status-success and db " server-name status db)
    (assoc-in db [:servers-status server-name] status)))

(re-frame/reg-event-db
  :server-status-error
  (fn [db [_ server-name]]
    (update-in db [:logs] (conj (str server-name "unreachable")))))

(re-frame/reg-event-fx
  :read-consul
  (fn []
    (GET "http://localhost:3450/ping/consul/services"
         {:handler       (fn [servers]
                           (doseq [{:keys [server-name status]} (reader/read-string servers)]
                             (re-frame/dispatch [:add-consul-server [server-name (str "consul/services/" server-name)]])
                             ))
          :error-handler #(println "error from /ping/consul/services : " %)
          })))

(re-frame/reg-event-db
  :add-consul-server
  (fn [db [_ [server-name url]]]
    (update db :servers conj [server-name url])))

(re-frame/reg-event-fx
  :refresh-all
  (fn [db]
    (doseq [[server-name url] (get-in db [:db :servers])]
      (GET (str "http://localhost:3450/ping/" url)
           {:handler       (fn [status]
                             (re-frame/dispatch [:server-status-success [server-name (:status (reader/read-string status))]]))
            :error-handler #(println "error from /ping/" url " :" %)
            }))))
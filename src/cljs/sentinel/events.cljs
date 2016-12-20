(ns sentinel.events
  (:require [re-frame.core :as re-frame]
            [sentinel.db :as db]))


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
    (println "in :server-status-success" server-name status)
    (assoc-in db [:servers-status server-name] status)))

(re-frame/reg-event-db
  :server-status-error
  (fn [db [_ server-name]]
    (update-in db [:logs] (conj (str server-name "unreachable")))))
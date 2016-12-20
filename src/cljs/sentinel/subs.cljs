(ns sentinel.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.ratom :refer [make-reaction]]
            [re-frame.core :as re-frame]
            [cljs.reader :as reader]
            [ajax.core :refer [GET]]))


(re-frame/reg-sub :get-name (fn [db] (:name db)))

(re-frame/reg-sub :get-servers (fn [db] (:servers db)))

(re-frame/reg-sub-raw
  :get-server-status
  (fn [db [_ [server-name url]]]
    (println "in :get-server-status" server-name url)
    (when url (GET (str "http://localhost:3450/ping/" url)
                   {:handler       (fn [x]
                                     (println "response :  " (:status (reader/read-string x)))
                                     (re-frame/dispatch [:server-status-success [server-name (-> x reader/read-string :status)]]))
                    :error-handler #(re-frame/dispatch [:server-status-error [server-name]])}))
    (reaction (get-in @db [:servers-status server-name]))))
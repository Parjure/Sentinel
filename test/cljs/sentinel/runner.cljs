(ns sentinel.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [sentinel.core-test]))

(doo-tests 'sentinel.core-test)

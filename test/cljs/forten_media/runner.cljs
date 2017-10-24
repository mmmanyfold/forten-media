(ns forten-media.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [forten-media.core-test]))

(doo-tests 'forten-media.core-test)

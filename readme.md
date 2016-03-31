Epoch Converter
===============

Java GUI app to convert dates and epoch time (in milliseconds) between each other.

Dates use the following format (Timezones are not accepted). Note that dates can be partially completed and
the missing elements will be assumed as zero. Entering only numbers will assume the input as being an epoch time

    Full date string
    yyyy-MM-dd HH:mm:ss,SSS
    
    Partial date strings
    yyyy-MM-dd HH:mm
    yyyy-MM-dd
    yyyy-
    
    Anything less is assumed as an epoch time


Date era (BC, AD) can be set to BC by clicking the checkbox marked 'BC'. Unchecked means AD.
This only applies when entering a date string, if entering an epoch time the era will be set automatically.

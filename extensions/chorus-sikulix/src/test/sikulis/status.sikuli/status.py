from sikuli.Sikuli import *

class status:

    def clickStartAutoUpdate(self):
       click("StartAutoUpdate.png")
       click("send.png")
       
     
    def getIntValue(self):
        return 123

    def sumValues(self, val1, val2):
        return val1 + val2

    def badCall(self, val1, val2):
        return ProblemCall


#status().clickStartAutoUpdate()
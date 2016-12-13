import urllib2
response = urllib2.urlopen('https://d3c33hcgiwev3.cloudfront.net/_9b0d0ff87935997de01d221fd74bae90_categories.txt?Expires=1481328000&Signature=ghAx~qGEqusNOCWoLqhSYAjHnsBn56gxvTIEPD1lZwImBHTNJwmAgoMkweYO719WoZH01WCMxtvMuca6XEnuQ87fjB8BPcLivAT3iSKCTzgES7uBFgDKQm5dbpmTzHd0NmuX8GJe-7cmzqlwS0lWU43xb1BfIJN1WQxCmAuIQ54_&Key-Pair-Id=APKAJLTNE6QMUY6HBC5A')

html = response.read()
if html[53] == "\n":
	print("OK")




def parse():
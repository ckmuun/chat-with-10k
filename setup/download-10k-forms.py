from sec_edgar_downloader import Downloader

print("Downloading 10k forms")
dl = Downloader("university", "my.email@domain.com")


ticker_list = ["MSFT", "AAPL", "GOOG", "AMZN", "NVDA", "AAL", "A", "TGT", "KO", "MMM", "HOG", "LUV", "C", "TXN" ]


for ticker in ticker_list:
    print("Downloading " + ticker)
    dl.get("10-K", ticker, limit=1)



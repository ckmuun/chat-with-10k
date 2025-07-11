from sec_edgar_downloader import Downloader

print("Downloading 10k forms")
dl = Downloader("university", "my.email@domain.com")


ticker_list = ["MSFT", "AAPL", "GOOG", "AMZN", "NV"]
dl.get("10-K", "MSFT", limit=1)



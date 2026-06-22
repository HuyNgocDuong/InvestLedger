unit uModels;

interface

type
  TInvestor = record
    Id: Int64;
    FirstName: string;
    LastName: string;
    Email: string;
  end;

  TPortfolio = record
    Id: Int64;
    InvestorId: Int64;
    Name: string;
    Currency: string;
    Status: string;
  end;

  TPortfolioSummary = record
    PortfolioId: Int64;
    InvestorId: Int64;
    TotalMarketValue: Double;
    TotalCost: Double;
    UnrealizedPnl: Double;
  end;

  TInvestorSummary = record
    InvestorId: Int64;
    TotalMarketValue: Double;
    TotalCost: Double;
    UnrealizedPnl: Double;
  end;

  THolding = record
    Id: Int64;
    InstrumentId: Int64;
    InstrumentSymbol: string;
    InstrumentName: string;
    Quantity: Double;
    AverageCost: Double;
    MarketValue: Double;
  end;

  TTransaction = record
    Id: Int64;
    PortfolioId: Int64;
    InstrumentId: Int64;
    TradeDate: string;
    TransactionType: string;
    Quantity: Double;
    Price: Double;
    NetAmount: Double;
    Currency: string;
    Notes: string;
  end;

implementation

end.

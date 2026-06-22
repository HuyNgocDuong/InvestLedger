unit uApiClient;

interface

uses
  System.SysUtils,
  System.Classes,
  System.JSON,
  System.Net.URLClient,
  System.Net.HttpClient,
  System.Net.HttpClientComponent,
  uModels;

const
  DEFAULT_API_KEY = 'dev-investledger-key';

type
  TInvestLedgerApiClient = class
  private
    FBaseUrl: string;
    FApiKey: string;
    FClient: TNetHTTPClient;
    function GetJson(const APath: string): TJSONValue;
    function JsonGetDouble(const AObject: TJSONObject; const AName: string): Double;
  public
    constructor Create(const ABaseUrl: string; const AApiKey: string = DEFAULT_API_KEY);
    destructor Destroy; override;

    property ApiKey: string read FApiKey write FApiKey;

    function GetInvestors: TArray<TInvestor>;
    function GetInvestorSummary(const AInvestorId: Int64): TInvestorSummary;
    function GetPortfolios(const AInvestorId: Int64): TArray<TPortfolio>;
    function GetPortfolioSummary(const APortfolioId: Int64): TPortfolioSummary;
    function GetHoldings(const APortfolioId: Int64): TArray<THolding>;
    function GetTransactions(const APortfolioId: Int64): TArray<TTransaction>;
  end;

implementation

constructor TInvestLedgerApiClient.Create(const ABaseUrl: string; const AApiKey: string);
begin
  inherited Create;
  FBaseUrl := ABaseUrl;
  FApiKey := AApiKey;
  FClient := TNetHTTPClient.Create(nil);
  FClient.ConnectionTimeout := 5000;
  FClient.ResponseTimeout := 15000;
end;

destructor TInvestLedgerApiClient.Destroy;
begin
  FClient.Free;
  inherited;
end;

function TInvestLedgerApiClient.GetJson(const APath: string): TJSONValue;
var
  Response: IHTTPResponse;
  Headers: TNetHeaders;
begin
  SetLength(Headers, 0);
  if FApiKey <> '' then
  begin
    SetLength(Headers, 1);
    Headers[0] := TNetHeader.Create('X-API-Key', FApiKey);
  end;
  Response := FClient.Get(FBaseUrl + APath, nil, Headers);
  if Response.StatusCode <> 200 then
    raise Exception.CreateFmt('API call failed: %s (%d) %s', [APath, Response.StatusCode, Response.ContentAsString(TEncoding.UTF8)]);
  Result := TJSONObject.ParseJSONValue(Response.ContentAsString(TEncoding.UTF8));
end;

function TInvestLedgerApiClient.JsonGetDouble(const AObject: TJSONObject; const AName: string): Double;
var
  Value: TJSONValue;
begin
  Value := AObject.GetValue(AName);
  if Value = nil then
    Exit(0);
  if Value is TJSONNumber then
    Result := TJSONNumber(Value).AsDouble
  else
    Result := StrToFloatDef(Value.Value, 0);
end;

function TInvestLedgerApiClient.GetInvestors: TArray<TInvestor>;
var
  JsonValue: TJSONValue;
  JsonArray: TJSONArray;
  JsonObject: TJSONObject;
  I: Integer;
begin
  JsonValue := GetJson('/api/investors');
  try
    if not (JsonValue is TJSONArray) then
      raise Exception.Create('Unexpected JSON for investors');
    JsonArray := JsonValue as TJSONArray;
    SetLength(Result, JsonArray.Count);
    for I := 0 to JsonArray.Count - 1 do
    begin
      JsonObject := JsonArray.Items[I] as TJSONObject;
      Result[I].Id := JsonObject.GetValue<Int64>('id');
      Result[I].FirstName := JsonObject.GetValue<string>('firstName');
      Result[I].LastName := JsonObject.GetValue<string>('lastName');
      Result[I].Email := JsonObject.GetValue<string>('email');
    end;
  finally
    JsonValue.Free;
  end;
end;

function TInvestLedgerApiClient.GetInvestorSummary(const AInvestorId: Int64): TInvestorSummary;
var
  JsonValue: TJSONValue;
  JsonObject: TJSONObject;
begin
  JsonValue := GetJson(Format('/api/investors/%d/summary', [AInvestorId]));
  try
    if not (JsonValue is TJSONObject) then
      raise Exception.Create('Unexpected JSON for investor summary');
    JsonObject := JsonValue as TJSONObject;
    Result.InvestorId := JsonObject.GetValue<Int64>('investorId');
    Result.TotalMarketValue := JsonGetDouble(JsonObject, 'totalMarketValue');
    Result.TotalCost := JsonGetDouble(JsonObject, 'totalCost');
    Result.UnrealizedPnl := JsonGetDouble(JsonObject, 'unrealizedPnl');
  finally
    JsonValue.Free;
  end;
end;

function TInvestLedgerApiClient.GetPortfolios(const AInvestorId: Int64): TArray<TPortfolio>;
var
  JsonValue: TJSONValue;
  JsonArray: TJSONArray;
  JsonObject: TJSONObject;
  I: Integer;
begin
  JsonValue := GetJson(Format('/api/investors/%d/portfolios', [AInvestorId]));
  try
    if not (JsonValue is TJSONArray) then
      raise Exception.Create('Unexpected JSON for portfolios');
    JsonArray := JsonValue as TJSONArray;
    SetLength(Result, JsonArray.Count);
    for I := 0 to JsonArray.Count - 1 do
    begin
      JsonObject := JsonArray.Items[I] as TJSONObject;
      Result[I].Id := JsonObject.GetValue<Int64>('id');
      Result[I].InvestorId := JsonObject.GetValue<Int64>('investorId');
      Result[I].Name := JsonObject.GetValue<string>('name');
      Result[I].Currency := JsonObject.GetValue<string>('currency');
      Result[I].Status := JsonObject.GetValue<string>('status');
    end;
  finally
    JsonValue.Free;
  end;
end;

function TInvestLedgerApiClient.GetPortfolioSummary(const APortfolioId: Int64): TPortfolioSummary;
var
  JsonValue: TJSONValue;
  JsonObject: TJSONObject;
begin
  JsonValue := GetJson(Format('/api/portfolios/%d/summary', [APortfolioId]));
  try
    if not (JsonValue is TJSONObject) then
      raise Exception.Create('Unexpected JSON for portfolio summary');
    JsonObject := JsonValue as TJSONObject;
    Result.PortfolioId := JsonObject.GetValue<Int64>('portfolioId');
    Result.InvestorId := JsonObject.GetValue<Int64>('investorId');
    Result.TotalMarketValue := JsonGetDouble(JsonObject, 'totalMarketValue');
    Result.TotalCost := JsonGetDouble(JsonObject, 'totalCost');
    Result.UnrealizedPnl := JsonGetDouble(JsonObject, 'unrealizedPnl');
  finally
    JsonValue.Free;
  end;
end;

function TInvestLedgerApiClient.GetHoldings(const APortfolioId: Int64): TArray<THolding>;
var
  JsonValue: TJSONValue;
  JsonArray: TJSONArray;
  JsonObject: TJSONObject;
  I: Integer;
begin
  JsonValue := GetJson(Format('/api/portfolios/%d/holdings', [APortfolioId]));
  try
    if not (JsonValue is TJSONArray) then
      raise Exception.Create('Unexpected JSON for holdings');
    JsonArray := JsonValue as TJSONArray;
    SetLength(Result, JsonArray.Count);
    for I := 0 to JsonArray.Count - 1 do
    begin
      JsonObject := JsonArray.Items[I] as TJSONObject;
      Result[I].Id := JsonObject.GetValue<Int64>('id');
      Result[I].InstrumentId := JsonObject.GetValue<Int64>('instrumentId');
      Result[I].InstrumentSymbol := JsonObject.GetValue<string>('instrumentSymbol');
      Result[I].InstrumentName := JsonObject.GetValue<string>('instrumentName');
      Result[I].Quantity := JsonGetDouble(JsonObject, 'quantity');
      Result[I].AverageCost := JsonGetDouble(JsonObject, 'averageCost');
      Result[I].MarketValue := JsonGetDouble(JsonObject, 'marketValue');
    end;
  finally
    JsonValue.Free;
  end;
end;

function TInvestLedgerApiClient.GetTransactions(const APortfolioId: Int64): TArray<TTransaction>;
var
  JsonValue: TJSONValue;
  JsonArray: TJSONArray;
  JsonObject: TJSONObject;
  I: Integer;
begin
  JsonValue := GetJson(Format('/api/portfolios/%d/transactions', [APortfolioId]));
  try
    if not (JsonValue is TJSONArray) then
      raise Exception.Create('Unexpected JSON for transactions');
    JsonArray := JsonValue as TJSONArray;
    SetLength(Result, JsonArray.Count);
    for I := 0 to JsonArray.Count - 1 do
    begin
      JsonObject := JsonArray.Items[I] as TJSONObject;
      Result[I].Id := JsonObject.GetValue<Int64>('id');
      Result[I].PortfolioId := JsonObject.GetValue<Int64>('portfolioId');
      Result[I].InstrumentId := JsonObject.GetValue<Int64>('instrumentId');
      Result[I].TradeDate := JsonObject.GetValue<string>('tradeDate');
      Result[I].TransactionType := JsonObject.GetValue<string>('transactionType');
      Result[I].Quantity := JsonGetDouble(JsonObject, 'quantity');
      Result[I].Price := JsonGetDouble(JsonObject, 'price');
      Result[I].NetAmount := JsonGetDouble(JsonObject, 'netAmount');
      Result[I].Currency := JsonObject.GetValue<string>('currency');
      if JsonObject.GetValue('notes') <> nil then
        Result[I].Notes := JsonObject.GetValue<string>('notes')
      else
        Result[I].Notes := '';
    end;
  finally
    JsonValue.Free;
  end;
end;

end.

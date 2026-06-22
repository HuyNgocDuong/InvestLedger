unit uMainForm;

interface

uses
  Winapi.Windows,
  Winapi.Messages,
  System.SysUtils,
  System.Variants,
  System.Classes,
  System.UITypes,
  Vcl.Graphics,
  Vcl.Controls,
  Vcl.Forms,
  Vcl.Dialogs,
  Vcl.StdCtrls,
  Vcl.ExtCtrls,
  Vcl.Grids,
  uApiClient,
  uModels;

type
  TMainForm = class(TForm)
    pnlTop: TPanel;
    lblApiKey: TLabel;
    edtApiKey: TEdit;
    btnRefresh: TButton;
    lblSummary: TLabel;
    pnlBody: TPanel;
    pnlLeft: TPanel;
    pnlRight: TPanel;
    lblInvestors: TLabel;
    grdInvestors: TStringGrid;
    lblPortfolios: TLabel;
    grdPortfolios: TStringGrid;
    lblHoldings: TLabel;
    grdHoldings: TStringGrid;
    lblTransactions: TLabel;
    grdTransactions: TStringGrid;
    procedure FormCreate(Sender: TObject);
    procedure FormDestroy(Sender: TObject);
    procedure btnRefreshClick(Sender: TObject);
    procedure grdInvestorsSelectCell(Sender: TObject; ACol, ARow: Integer; var CanSelect: Boolean);
    procedure grdPortfoliosSelectCell(Sender: TObject; ACol, ARow: Integer; var CanSelect: Boolean);
  private
    FApiClient: TInvestLedgerApiClient;
    FInvestors: TArray<TInvestor>;
    FPortfolios: TArray<TPortfolio>;
    procedure SetupGrids;
    procedure ClearGrid(const AGrid: TStringGrid);
    procedure LoadInvestors;
    procedure LoadPortfolios(const AInvestorIndex: Integer);
    procedure LoadPortfolioDetails(const APortfolioIndex: Integer);
    procedure UpdateSummary(const AText: string);
    procedure ShowError(const AMessage: string);
  public
  end;

var
  MainForm: TMainForm;

implementation

{$R *.dfm}

procedure TMainForm.FormCreate(Sender: TObject);
begin
  Caption := 'InvestLedger Client';
  Width := 1100;
  Height := 700;
  Position := poScreenCenter;

  edtApiKey.Text := DEFAULT_API_KEY;
  FApiClient := TInvestLedgerApiClient.Create('http://localhost:8080', DEFAULT_API_KEY);
  SetupGrids;
  UpdateSummary('Select an investor to view portfolio data.');
end;

procedure TMainForm.FormDestroy(Sender: TObject);
begin
  FreeAndNil(FApiClient);
end;

procedure TMainForm.SetupGrids;
begin
  grdInvestors.FixedRows := 1;
  grdInvestors.ColCount := 4;
  grdInvestors.Cells[0, 0] := 'ID';
  grdInvestors.Cells[1, 0] := 'First Name';
  grdInvestors.Cells[2, 0] := 'Last Name';
  grdInvestors.Cells[3, 0] := 'Email';

  grdPortfolios.FixedRows := 1;
  grdPortfolios.ColCount := 4;
  grdPortfolios.Cells[0, 0] := 'ID';
  grdPortfolios.Cells[1, 0] := 'Name';
  grdPortfolios.Cells[2, 0] := 'Currency';
  grdPortfolios.Cells[3, 0] := 'Status';

  grdHoldings.FixedRows := 1;
  grdHoldings.ColCount := 5;
  grdHoldings.Cells[0, 0] := 'Symbol';
  grdHoldings.Cells[1, 0] := 'Name';
  grdHoldings.Cells[2, 0] := 'Qty';
  grdHoldings.Cells[3, 0] := 'Avg Cost';
  grdHoldings.Cells[4, 0] := 'Market Value';

  grdTransactions.FixedRows := 1;
  grdTransactions.ColCount := 7;
  grdTransactions.Cells[0, 0] := 'Date';
  grdTransactions.Cells[1, 0] := 'Type';
  grdTransactions.Cells[2, 0] := 'Instrument';
  grdTransactions.Cells[3, 0] := 'Qty';
  grdTransactions.Cells[4, 0] := 'Price';
  grdTransactions.Cells[5, 0] := 'Net';
  grdTransactions.Cells[6, 0] := 'Notes';
end;

procedure TMainForm.ClearGrid(const AGrid: TStringGrid);
begin
  AGrid.RowCount := AGrid.FixedRows;
end;

procedure TMainForm.UpdateSummary(const AText: string);
begin
  lblSummary.Caption := AText;
end;

procedure TMainForm.ShowError(const AMessage: string);
begin
  UpdateSummary('Error: ' + AMessage);
  MessageDlg(AMessage, mtError, [mbOK], 0);
end;

procedure TMainForm.btnRefreshClick(Sender: TObject);
begin
  FApiClient.ApiKey := Trim(edtApiKey.Text);
  LoadInvestors;
end;

procedure TMainForm.LoadInvestors;
var
  I: Integer;
  InvestorSummary: TInvestorSummary;
begin
  ClearGrid(grdPortfolios);
  ClearGrid(grdHoldings);
  ClearGrid(grdTransactions);
  SetLength(FPortfolios, 0);

  try
    FInvestors := FApiClient.GetInvestors;
    grdInvestors.RowCount := Length(FInvestors) + 1;
    for I := 0 to High(FInvestors) do
    begin
      grdInvestors.Cells[0, I + 1] := IntToStr(FInvestors[I].Id);
      grdInvestors.Cells[1, I + 1] := FInvestors[I].FirstName;
      grdInvestors.Cells[2, I + 1] := FInvestors[I].LastName;
      grdInvestors.Cells[3, I + 1] := FInvestors[I].Email;
    end;

    if Length(FInvestors) > 0 then
    begin
      grdInvestors.Row := 1;
      InvestorSummary := FApiClient.GetInvestorSummary(FInvestors[0].Id);
      UpdateSummary(Format('Loaded %d investors. Investor #%d total value: %.2f',
        [Length(FInvestors), InvestorSummary.InvestorId, InvestorSummary.TotalMarketValue]));
      LoadPortfolios(0);
    end
    else
      UpdateSummary('No investors found.');
  except
    on E: Exception do
      ShowError(E.Message);
  end;
end;

procedure TMainForm.LoadPortfolios(const AInvestorIndex: Integer);
var
  I: Integer;
  InvestorSummary: TInvestorSummary;
begin
  ClearGrid(grdPortfolios);
  ClearGrid(grdHoldings);
  ClearGrid(grdTransactions);

  if (AInvestorIndex < 0) or (AInvestorIndex > High(FInvestors)) then
    Exit;

  try
    FPortfolios := FApiClient.GetPortfolios(FInvestors[AInvestorIndex].Id);
    grdPortfolios.RowCount := Length(FPortfolios) + 1;
    for I := 0 to High(FPortfolios) do
    begin
      grdPortfolios.Cells[0, I + 1] := IntToStr(FPortfolios[I].Id);
      grdPortfolios.Cells[1, I + 1] := FPortfolios[I].Name;
      grdPortfolios.Cells[2, I + 1] := FPortfolios[I].Currency;
      grdPortfolios.Cells[3, I + 1] := FPortfolios[I].Status;
    end;

    InvestorSummary := FApiClient.GetInvestorSummary(FInvestors[AInvestorIndex].Id);
    UpdateSummary(Format('Investor #%d (%s %s) | Total Value: %.2f | Cost: %.2f | P/L: %.2f',
      [FInvestors[AInvestorIndex].Id,
       FInvestors[AInvestorIndex].FirstName,
       FInvestors[AInvestorIndex].LastName,
       InvestorSummary.TotalMarketValue,
       InvestorSummary.TotalCost,
       InvestorSummary.UnrealizedPnl]));

    if Length(FPortfolios) > 0 then
    begin
      grdPortfolios.Row := 1;
      LoadPortfolioDetails(0);
    end;
  except
    on E: Exception do
      ShowError(E.Message);
  end;
end;

procedure TMainForm.LoadPortfolioDetails(const APortfolioIndex: Integer);
var
  Holdings: TArray<THolding>;
  Transactions: TArray<TTransaction>;
  Summary: TPortfolioSummary;
  I: Integer;
begin
  ClearGrid(grdHoldings);
  ClearGrid(grdTransactions);

  if (APortfolioIndex < 0) or (APortfolioIndex > High(FPortfolios)) then
    Exit;

  try
    Holdings := FApiClient.GetHoldings(FPortfolios[APortfolioIndex].Id);
    Transactions := FApiClient.GetTransactions(FPortfolios[APortfolioIndex].Id);
    Summary := FApiClient.GetPortfolioSummary(FPortfolios[APortfolioIndex].Id);

    grdHoldings.RowCount := Length(Holdings) + 1;
    for I := 0 to High(Holdings) do
    begin
      grdHoldings.Cells[0, I + 1] := Holdings[I].InstrumentSymbol;
      grdHoldings.Cells[1, I + 1] := Holdings[I].InstrumentName;
      grdHoldings.Cells[2, I + 1] := FormatFloat('0.####', Holdings[I].Quantity);
      grdHoldings.Cells[3, I + 1] := FormatFloat('0.00', Holdings[I].AverageCost);
      grdHoldings.Cells[4, I + 1] := FormatFloat('0.00', Holdings[I].MarketValue);
    end;

    grdTransactions.RowCount := Length(Transactions) + 1;
    for I := 0 to High(Transactions) do
    begin
      grdTransactions.Cells[0, I + 1] := Transactions[I].TradeDate;
      grdTransactions.Cells[1, I + 1] := Transactions[I].TransactionType;
      grdTransactions.Cells[2, I + 1] := IntToStr(Transactions[I].InstrumentId);
      grdTransactions.Cells[3, I + 1] := FormatFloat('0.####', Transactions[I].Quantity);
      grdTransactions.Cells[4, I + 1] := FormatFloat('0.00', Transactions[I].Price);
      grdTransactions.Cells[5, I + 1] := FormatFloat('0.00', Transactions[I].NetAmount);
      grdTransactions.Cells[6, I + 1] := Transactions[I].Notes;
    end;

    UpdateSummary(Format('Portfolio #%d (%s) | Value: %.2f | Cost: %.2f | P/L: %.2f | Holdings: %d | Transactions: %d',
      [Summary.PortfolioId,
       FPortfolios[APortfolioIndex].Name,
       Summary.TotalMarketValue,
       Summary.TotalCost,
       Summary.UnrealizedPnl,
       Length(Holdings),
       Length(Transactions)]));
  except
    on E: Exception do
      ShowError(E.Message);
  end;
end;

procedure TMainForm.grdInvestorsSelectCell(Sender: TObject; ACol, ARow: Integer; var CanSelect: Boolean);
begin
  if ARow > 0 then
    LoadPortfolios(ARow - 1);
end;

procedure TMainForm.grdPortfoliosSelectCell(Sender: TObject; ACol, ARow: Integer; var CanSelect: Boolean);
begin
  if ARow > 0 then
    LoadPortfolioDetails(ARow - 1);
end;

end.

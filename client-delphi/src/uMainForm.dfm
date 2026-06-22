object MainForm: TMainForm
  Left = 0
  Top = 0
  Caption = 'InvestLedger Client'
  ClientHeight = 700
  ClientWidth = 1100
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -12
  Font.Name = 'Segoe UI'
  Font.Style = []
  OnCreate = FormCreate
  OnDestroy = FormDestroy
  TextHeight = 15
  object pnlTop: TPanel
    Left = 0
    Top = 0
    Width = 1100
    Height = 72
    Align = alTop
    BevelOuter = bvNone
    TabOrder = 0
    object lblApiKey: TLabel
      Left = 16
      Top = 16
      Width = 44
      Height = 15
      Caption = 'API Key'
    end
    object edtApiKey: TEdit
      Left = 72
      Top = 12
      Width = 280
      Height = 23
      TabOrder = 0
    end
    object btnRefresh: TButton
      Left = 368
      Top = 11
      Width = 100
      Height = 25
      Caption = 'Refresh'
      TabOrder = 1
      OnClick = btnRefreshClick
    end
    object lblSummary: TLabel
      Left = 16
      Top = 44
      Width = 1068
      Height = 15
      AutoSize = False
      Caption = 'Summary'
    end
  end
  object pnlBody: TPanel
    Left = 0
    Top = 72
    Width = 1100
    Height = 628
    Align = alClient
    BevelOuter = bvNone
    TabOrder = 1
    object pnlLeft: TPanel
      Left = 0
      Top = 0
      Width = 548
      Height = 628
      Align = alLeft
      BevelOuter = bvNone
      TabOrder = 0
      object lblInvestors: TLabel
        Left = 8
        Top = 8
        Width = 49
        Height = 15
        Caption = 'Investors'
      end
      object grdInvestors: TStringGrid
        Left = 8
        Top = 28
        Width = 532
        Height = 180
        ColCount = 4
        DefaultRowHeight = 22
        FixedCols = 0
        RowCount = 2
        Options = [goFixedVertLine, goFixedHorzLine, goVertLine, goHorzLine, goRangeSelect, goRowSelect]
        TabOrder = 0
        OnSelectCell = grdInvestorsSelectCell
      end
      object lblHoldings: TLabel
        Left = 8
        Top = 220
        Width = 48
        Height = 15
        Caption = 'Holdings'
      end
      object grdHoldings: TStringGrid
        Left = 8
        Top = 240
        Width = 532
        Height = 380
        ColCount = 5
        DefaultRowHeight = 22
        FixedCols = 0
        RowCount = 2
        Options = [goFixedVertLine, goFixedHorzLine, goVertLine, goHorzLine, goRangeSelect, goRowSelect]
        TabOrder = 1
      end
    end
    object pnlRight: TPanel
      Left = 548
      Top = 0
      Width = 552
      Height = 628
      Align = alClient
      BevelOuter = bvNone
      TabOrder = 1
      object lblPortfolios: TLabel
        Left = 8
        Top = 8
        Width = 55
        Height = 15
        Caption = 'Portfolios'
      end
      object grdPortfolios: TStringGrid
        Left = 8
        Top = 28
        Width = 536
        Height = 180
        ColCount = 4
        DefaultRowHeight = 22
        FixedCols = 0
        RowCount = 2
        Options = [goFixedVertLine, goFixedHorzLine, goVertLine, goHorzLine, goRangeSelect, goRowSelect]
        TabOrder = 0
        OnSelectCell = grdPortfoliosSelectCell
      end
      object lblTransactions: TLabel
        Left = 8
        Top = 220
        Width = 68
        Height = 15
        Caption = 'Transactions'
      end
      object grdTransactions: TStringGrid
        Left = 8
        Top = 240
        Width = 536
        Height = 380
        ColCount = 7
        DefaultRowHeight = 22
        FixedCols = 0
        RowCount = 2
        Options = [goFixedVertLine, goFixedHorzLine, goVertLine, goHorzLine, goRangeSelect, goRowSelect]
        TabOrder = 1
      end
    end
  end
end

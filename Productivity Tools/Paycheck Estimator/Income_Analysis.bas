Attribute VB_Name = "v04"
Dim oApp As New Outlook.Application
Dim Session As Outlook.Namespace
Dim AppointmentsFolder As Outlook.Folder

Dim CompanyStrings
Dim NumCompanies As Integer
Dim Companies As New Dictionary
Dim Shts() As Worksheet
Dim Tbls() As ListObject
Dim CutoffDate As Date
Dim Marker As Range
Dim LastImportDateField As Range

Sub Synchronize()

    Call Setup
    Call Import
    Call LeftMatch
    Call RightMatch
    Call CleanUp
    
End Sub
Function Setup()
    
    'Debug.Print "conducting setup"
    
    'Start Outlook and retrieve Work Schedule folder.
    Set Session = oApp.Session
    Set AppointmentsFolder = Session.Folders("danicaroberts@outlook.com").Folders("Calendar").Folders("Work Schedule")
    
    CompanyStrings = Array("DTNA", "WGL", "MFI", "TS")
    NumCompanies = UBound(CompanyStrings) - LBound(CompanyStrings) + 1
    ReDim Shts(NumCompanies)
    ReDim Tbls(NumCompanies)
    
    'Set up storage structures.
    For i = 0 To NumCompanies - 1
        Companies.Add CompanyStrings(i), New SortedList
        Set Shts(i) = ThisWorkbook.Worksheets("Calendar Imports - " & CompanyStrings(i))
        Set Tbls(i) = Shts(i).ListObjects("Calendar_Imports_" & CompanyStrings(i))
    Next
    
    'Get cutoff date from Constants sheet.
    Set LastImportDateField = Range("Constants!Last_Import")
    
    CutoffDate = LastImportDateField.Value - 7
    'CutoffDate = #10/1/2019#
    Set Marker = Sheet1.Range("A2")
    
End Function
Function Import()
    
    Dim TargetItems As Outlook.Items
    Dim temp As String
    
    For i = 0 To NumCompanies - 1
        
        'Debug.Print "importing " & i
        
        'Filter = "[Class] = 'olAppointment'"
        'And [Categories] = " & CompanyStrings(i) & " And [Start] >= " & CutoffDate
        Set TargetItems = AppointmentsFolder.Items.Restrict("[Categories] = " & CompanyStrings(i))
        
        
        
        For Each Appointment In TargetItems
        
            If Appointment.Start > CutoffDate Then
                
                Companies.Item(CompanyStrings(i)).Add Appointment.Start, Appointment
                'Debug.Print Appointment.Subject, Appointment.Categories, Appointment.Start
                
                
            
            End If
        Next
        
    Next
    
    Set TargetItems = Nothing
    
End Function
Function LeftMatch()
    
    For i = 0 To NumCompanies - 1
        
        'Debug.Print "matching left " & i
        
        'Sort Table in ascending order.
        Call SortTable(Tbls(i), xlAscending)
        
        'Table index.
        tIndex = 2
        
        'Find the first cell that is greater than or equal to the CutoffDate.
        If Not IsEmpty(Tbls(i).Range(tIndex, Tbls(i).ListColumns("Start").index).Value) Then
            Do While (Tbls(i).Range(tIndex, Tbls(i).ListColumns("Start").index).Value < CutoffDate) And (tIndex < 1000)
                tIndex = tIndex + 1
            Loop
        
            
            'Compare the Table's contents to the SortedArray contents.
            'If Table's contents cannot be found in SortedArray, delete that row. Otherwise, if there
            'is already a matching record in the Table, remove the AppointmentItem from the SortedArray.
            
            Do While tIndex < Tbls(i).Range.Rows.Count + 1
                
                'Must have nested "If" here because the "If" statement does not terminate after first false value,
                'and continues to try to compare the rest of its conditions when there isn't an object to compare with.
                 If Tbls(i).Range(tIndex, Tbls(i).ListColumns("Start").index).Value <> "" Then
                    
                    If Companies.Items()(i).Contains(Tbls(i).Range(tIndex, Tbls(i).ListColumns("Start").index).Value) Then
                    
                        If (Companies.Items()(i).Item(Tbls(i).Range(tIndex, Tbls(i).ListColumns("Start").index).Value).End = Tbls(i).Range(tIndex, Tbls(i).ListColumns("End").index).Value And _
                           Companies.Items()(i).Item(Tbls(i).Range(tIndex, Tbls(i).ListColumns("Start").index).Value).Subject = Tbls(i).Range(tIndex, Tbls(i).ListColumns("Subject").index).Formula And _
                           CompanyStrings(i) = Tbls(i).Range(tIndex, Tbls(i).ListColumns("Category").index).Formula) Then
                                       
                            Companies.Items()(i).Remove Tbls(i).Range(tIndex, Tbls(i).ListColumns("Start").index).Value
                            tIndex = tIndex + 1
                            
                        Else
                            
                            Tbls(i).ListRows(tIndex - 1).Delete
                            
                        End If
                        
                    Else
                        
                        Tbls(i).ListRows(tIndex - 1).Delete
                        
                    End If
                    
                End If
                
            Loop
        End If
    Next i
       
End Function
Function RightMatch()
    
    
    For i = 0 To NumCompanies - 1
    
        'Debug.Print "matching right " & i
        
        If Companies.Items()(i).Count > 0 Then
        
            For j = 0 To Companies.Items()(i).Count - 1
                
                'Debug.Print "matching record" & j
                
                With Tbls(i)
                    .ListRows.Add
                    .Range(Tbls(i).Range.Rows.Count, Tbls(i).ListColumns("Subject").index).Value = Companies.Items()(i).GetByIndex(j).Subject
                    .Range(Tbls(i).Range.Rows.Count, Tbls(i).ListColumns("Start").index).Value = Companies.Items()(i).GetByIndex(j).Start
                    .Range(Tbls(i).Range.Rows.Count, Tbls(i).ListColumns("End").index).Value = Companies.Items()(i).GetByIndex(j).End
                    .Range(Tbls(i).Range.Rows.Count, Tbls(i).ListColumns("Category").index).Value = CompanyStrings(i)
                End With
                
                
                If CompanyStrings(i) <> "DTNA" Then
                
                    If Companies.Items()(i).GetByIndex(j).Subject <> "Werk" Then
                        
                        If CompanyStrings(i) = "WGL" Then
                            Tbls(i).Range(Tbls(i).Range.Rows.Count, Tbls(i).ListColumns("Amount").index).Value = Mid(Companies.Items()(i).GetByIndex(j).Body, InStr(Companies.Items()(i).GetByIndex(j).Body, "USD ") + Len("USD "), 5)
                            'Mid(temp, InStr(temp, "USD ") + Len("USD "), 5)
                        ElseIf CompanyStrings(i) = "MFI" Then
                            Tbls(i).Range(Tbls(i).Range.Rows.Count, Tbls(i).ListColumns("Amount").index).Value = Mid(Companies.Items()(i).GetByIndex(j).Body, InStr(Companies.Items()(i).GetByIndex(j).Body, "$") + Len("$"), 5)
                        End If
                    End If
                
                End If
                
            Next j
            
        End If
        
    Next i
    
End Function
Function CleanUp()
    
    'Debug.Print "cleaning up"
    Set Companies = Nothing
    
    For i = 0 To NumCompanies - 1
    
        Range(Tbls(i).Name).RemoveDuplicates Columns:=Array(1, 2, 3, 4, 5, 6, 7), Header:=xlNo
        
        Call SortTable(Tbls(i), xlDescending)
    
    Next i
    
    LastImportDateField.Value = Now
    ThisWorkbook.Connections("Query - Expected_Payouts_All").Refresh
    
End Function
    
Function SortTable(Table As ListObject, SortOrder As XlSortOrder)
    With Table.Sort
        .SortFields.Clear
        .SortFields.Add key:=Range(Table.Name & "[Start]"), SortOn:=xlSortOnValues, Order:=SortOrder
        .Header = xlYes
        .Apply
    End With
    
End Function



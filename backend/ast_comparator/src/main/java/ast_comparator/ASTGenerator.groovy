package ast_comparator

import ast_comparator.antlr.Java8Parser
import ast_comparator.antlr.Java8Visitor
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * Created by dfm114 on 28/10/16.
 */
class ASTGenerator implements Java8Visitor {
    @Override
    Object visitLiteral(Java8Parser.LiteralContext ctx) {
        return null
    }

    @Override
    Object visitType(Java8Parser.TypeContext ctx) {
        return null
    }

    @Override
    Object visitPrimitiveType(Java8Parser.PrimitiveTypeContext ctx) {
        return null
    }

    @Override
    Object visitNumericType(Java8Parser.NumericTypeContext ctx) {
        return null
    }

    @Override
    Object visitIntegralType(Java8Parser.IntegralTypeContext ctx) {
        return null
    }

    @Override
    Object visitFloatingPointType(Java8Parser.FloatingPointTypeContext ctx) {
        return null
    }

    @Override
    Object visitReferenceType(Java8Parser.ReferenceTypeContext ctx) {
        return null
    }

    @Override
    Object visitClassOrInterfaceType(Java8Parser.ClassOrInterfaceTypeContext ctx) {
        return null
    }

    @Override
    Object visitClassType(Java8Parser.ClassTypeContext ctx) {
        return null
    }

    @Override
    Object visitClassType_lf_classOrInterfaceType(Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) {
        return null
    }

    @Override
    Object visitClassType_lfno_classOrInterfaceType(Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) {
        return null
    }

    @Override
    Object visitInterfaceType(Java8Parser.InterfaceTypeContext ctx) {
        return null
    }

    @Override
    Object visitInterfaceType_lf_classOrInterfaceType(Java8Parser.InterfaceType_lf_classOrInterfaceTypeContext ctx) {
        return null
    }

    @Override
    Object visitInterfaceType_lfno_classOrInterfaceType(Java8Parser.InterfaceType_lfno_classOrInterfaceTypeContext ctx) {
        return null
    }

    @Override
    Object visitTypeVariable(Java8Parser.TypeVariableContext ctx) {
        return null
    }

    @Override
    Object visitArrayType(Java8Parser.ArrayTypeContext ctx) {
        return null
    }

    @Override
    Object visitDims(Java8Parser.DimsContext ctx) {
        return null
    }

    @Override
    Object visitTypeParameter(Java8Parser.TypeParameterContext ctx) {
        return null
    }

    @Override
    Object visitTypeParameterModifier(Java8Parser.TypeParameterModifierContext ctx) {
        return null
    }

    @Override
    Object visitTypeBound(Java8Parser.TypeBoundContext ctx) {
        return null
    }

    @Override
    Object visitAdditionalBound(Java8Parser.AdditionalBoundContext ctx) {
        return null
    }

    @Override
    Object visitTypeArguments(Java8Parser.TypeArgumentsContext ctx) {
        return null
    }

    @Override
    Object visitTypeArgumentList(Java8Parser.TypeArgumentListContext ctx) {
        return null
    }

    @Override
    Object visitTypeArgument(Java8Parser.TypeArgumentContext ctx) {
        return null
    }

    @Override
    Object visitWildcard(Java8Parser.WildcardContext ctx) {
        return null
    }

    @Override
    Object visitWildcardBounds(Java8Parser.WildcardBoundsContext ctx) {
        return null
    }

    @Override
    Object visitPackageName(Java8Parser.PackageNameContext ctx) {
        return null
    }

    @Override
    Object visitTypeName(Java8Parser.TypeNameContext ctx) {
        return null
    }

    @Override
    Object visitPackageOrTypeName(Java8Parser.PackageOrTypeNameContext ctx) {
        return null
    }

    @Override
    Object visitExpressionName(Java8Parser.ExpressionNameContext ctx) {
        return null
    }

    @Override
    Object visitMethodName(Java8Parser.MethodNameContext ctx) {
        return null
    }

    @Override
    Object visitAmbiguousName(Java8Parser.AmbiguousNameContext ctx) {
        return null
    }

    @Override
    Object visitCompilationUnit(Java8Parser.CompilationUnitContext ctx) {
        return null
    }

    @Override
    Object visitPackageDeclaration(Java8Parser.PackageDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitPackageModifier(Java8Parser.PackageModifierContext ctx) {
        return null
    }

    @Override
    Object visitImportDeclaration(Java8Parser.ImportDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitSingleTypeImportDeclaration(Java8Parser.SingleTypeImportDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitTypeImportOnDemandDeclaration(Java8Parser.TypeImportOnDemandDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitSingleStaticImportDeclaration(Java8Parser.SingleStaticImportDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitStaticImportOnDemandDeclaration(Java8Parser.StaticImportOnDemandDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitTypeDeclaration(Java8Parser.TypeDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitClassDeclaration(Java8Parser.ClassDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitClassModifier(Java8Parser.ClassModifierContext ctx) {
        return null
    }

    @Override
    Object visitTypeParameters(Java8Parser.TypeParametersContext ctx) {
        return null
    }

    @Override
    Object visitTypeParameterList(Java8Parser.TypeParameterListContext ctx) {
        return null
    }

    @Override
    Object visitSuperclass(Java8Parser.SuperclassContext ctx) {
        return null
    }

    @Override
    Object visitSuperinterfaces(Java8Parser.SuperinterfacesContext ctx) {
        return null
    }

    @Override
    Object visitInterfaceTypeList(Java8Parser.InterfaceTypeListContext ctx) {
        return null
    }

    @Override
    Object visitClassBody(Java8Parser.ClassBodyContext ctx) {
        return null
    }

    @Override
    Object visitClassBodyDeclaration(Java8Parser.ClassBodyDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitClassMemberDeclaration(Java8Parser.ClassMemberDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitFieldDeclaration(Java8Parser.FieldDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitFieldModifier(Java8Parser.FieldModifierContext ctx) {
        return null
    }

    @Override
    Object visitVariableDeclaratorList(Java8Parser.VariableDeclaratorListContext ctx) {
        return null
    }

    @Override
    Object visitVariableDeclarator(Java8Parser.VariableDeclaratorContext ctx) {
        return null
    }

    @Override
    Object visitVariableDeclaratorId(Java8Parser.VariableDeclaratorIdContext ctx) {
        return null
    }

    @Override
    Object visitVariableInitializer(Java8Parser.VariableInitializerContext ctx) {
        return null
    }

    @Override
    Object visitUnannType(Java8Parser.UnannTypeContext ctx) {
        return null
    }

    @Override
    Object visitUnannPrimitiveType(Java8Parser.UnannPrimitiveTypeContext ctx) {
        return null
    }

    @Override
    Object visitUnannReferenceType(Java8Parser.UnannReferenceTypeContext ctx) {
        return null
    }

    @Override
    Object visitUnannClassOrInterfaceType(Java8Parser.UnannClassOrInterfaceTypeContext ctx) {
        return null
    }

    @Override
    Object visitUnannClassType(Java8Parser.UnannClassTypeContext ctx) {
        return null
    }

    @Override
    Object visitUnannClassType_lf_unannClassOrInterfaceType(Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx) {
        return null
    }

    @Override
    Object visitUnannClassType_lfno_unannClassOrInterfaceType(Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) {
        return null
    }

    @Override
    Object visitUnannInterfaceType(Java8Parser.UnannInterfaceTypeContext ctx) {
        return null
    }

    @Override
    Object visitUnannInterfaceType_lf_unannClassOrInterfaceType(Java8Parser.UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx) {
        return null
    }

    @Override
    Object visitUnannInterfaceType_lfno_unannClassOrInterfaceType(Java8Parser.UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx) {
        return null
    }

    @Override
    Object visitUnannTypeVariable(Java8Parser.UnannTypeVariableContext ctx) {
        return null
    }

    @Override
    Object visitUnannArrayType(Java8Parser.UnannArrayTypeContext ctx) {
        return null
    }

    @Override
    Object visitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitMethodModifier(Java8Parser.MethodModifierContext ctx) {
        return null
    }

    @Override
    Object visitMethodHeader(Java8Parser.MethodHeaderContext ctx) {
        return null
    }

    @Override
    Object visitResult(Java8Parser.ResultContext ctx) {
        return null
    }

    @Override
    Object visitMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx) {
        return null
    }

    @Override
    Object visitFormalParameterList(Java8Parser.FormalParameterListContext ctx) {
        return null
    }

    @Override
    Object visitFormalParameters(Java8Parser.FormalParametersContext ctx) {
        return null
    }

    @Override
    Object visitFormalParameter(Java8Parser.FormalParameterContext ctx) {
        return null
    }

    @Override
    Object visitVariableModifier(Java8Parser.VariableModifierContext ctx) {
        return null
    }

    @Override
    Object visitLastFormalParameter(Java8Parser.LastFormalParameterContext ctx) {
        return null
    }

    @Override
    Object visitReceiverParameter(Java8Parser.ReceiverParameterContext ctx) {
        return null
    }

    @Override
    Object visitThrows_(Java8Parser.Throws_Context ctx) {
        return null
    }

    @Override
    Object visitExceptionTypeList(Java8Parser.ExceptionTypeListContext ctx) {
        return null
    }

    @Override
    Object visitExceptionType(Java8Parser.ExceptionTypeContext ctx) {
        return null
    }

    @Override
    Object visitMethodBody(Java8Parser.MethodBodyContext ctx) {
        return null
    }

    @Override
    Object visitInstanceInitializer(Java8Parser.InstanceInitializerContext ctx) {
        return null
    }

    @Override
    Object visitStaticInitializer(Java8Parser.StaticInitializerContext ctx) {
        return null
    }

    @Override
    Object visitConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitConstructorModifier(Java8Parser.ConstructorModifierContext ctx) {
        return null
    }

    @Override
    Object visitConstructorDeclarator(Java8Parser.ConstructorDeclaratorContext ctx) {
        return null
    }

    @Override
    Object visitSimpleTypeName(Java8Parser.SimpleTypeNameContext ctx) {
        return null
    }

    @Override
    Object visitConstructorBody(Java8Parser.ConstructorBodyContext ctx) {
        return null
    }

    @Override
    Object visitExplicitConstructorInvocation(Java8Parser.ExplicitConstructorInvocationContext ctx) {
        return null
    }

    @Override
    Object visitEnumDeclaration(Java8Parser.EnumDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitEnumBody(Java8Parser.EnumBodyContext ctx) {
        return null
    }

    @Override
    Object visitEnumConstantList(Java8Parser.EnumConstantListContext ctx) {
        return null
    }

    @Override
    Object visitEnumConstant(Java8Parser.EnumConstantContext ctx) {
        return null
    }

    @Override
    Object visitEnumConstantModifier(Java8Parser.EnumConstantModifierContext ctx) {
        return null
    }

    @Override
    Object visitEnumBodyDeclarations(Java8Parser.EnumBodyDeclarationsContext ctx) {
        return null
    }

    @Override
    Object visitInterfaceDeclaration(Java8Parser.InterfaceDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitInterfaceModifier(Java8Parser.InterfaceModifierContext ctx) {
        return null
    }

    @Override
    Object visitExtendsInterfaces(Java8Parser.ExtendsInterfacesContext ctx) {
        return null
    }

    @Override
    Object visitInterfaceBody(Java8Parser.InterfaceBodyContext ctx) {
        return null
    }

    @Override
    Object visitInterfaceMemberDeclaration(Java8Parser.InterfaceMemberDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitConstantDeclaration(Java8Parser.ConstantDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitConstantModifier(Java8Parser.ConstantModifierContext ctx) {
        return null
    }

    @Override
    Object visitInterfaceMethodDeclaration(Java8Parser.InterfaceMethodDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitInterfaceMethodModifier(Java8Parser.InterfaceMethodModifierContext ctx) {
        return null
    }

    @Override
    Object visitAnnotationTypeDeclaration(Java8Parser.AnnotationTypeDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitAnnotationTypeBody(Java8Parser.AnnotationTypeBodyContext ctx) {
        return null
    }

    @Override
    Object visitAnnotationTypeMemberDeclaration(Java8Parser.AnnotationTypeMemberDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitAnnotationTypeElementDeclaration(Java8Parser.AnnotationTypeElementDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitAnnotationTypeElementModifier(Java8Parser.AnnotationTypeElementModifierContext ctx) {
        return null
    }

    @Override
    Object visitDefaultValue(Java8Parser.DefaultValueContext ctx) {
        return null
    }

    @Override
    Object visitAnnotation(Java8Parser.AnnotationContext ctx) {
        return null
    }

    @Override
    Object visitNormalAnnotation(Java8Parser.NormalAnnotationContext ctx) {
        return null
    }

    @Override
    Object visitElementValuePairList(Java8Parser.ElementValuePairListContext ctx) {
        return null
    }

    @Override
    Object visitElementValuePair(Java8Parser.ElementValuePairContext ctx) {
        return null
    }

    @Override
    Object visitElementValue(Java8Parser.ElementValueContext ctx) {
        return null
    }

    @Override
    Object visitElementValueArrayInitializer(Java8Parser.ElementValueArrayInitializerContext ctx) {
        return null
    }

    @Override
    Object visitElementValueList(Java8Parser.ElementValueListContext ctx) {
        return null
    }

    @Override
    Object visitMarkerAnnotation(Java8Parser.MarkerAnnotationContext ctx) {
        return null
    }

    @Override
    Object visitSingleElementAnnotation(Java8Parser.SingleElementAnnotationContext ctx) {
        return null
    }

    @Override
    Object visitArrayInitializer(Java8Parser.ArrayInitializerContext ctx) {
        return null
    }

    @Override
    Object visitVariableInitializerList(Java8Parser.VariableInitializerListContext ctx) {
        return null
    }

    @Override
    Object visitBlock(Java8Parser.BlockContext ctx) {
        return null
    }

    @Override
    Object visitBlockStatements(Java8Parser.BlockStatementsContext ctx) {
        return null
    }

    @Override
    Object visitBlockStatement(Java8Parser.BlockStatementContext ctx) {
        return null
    }

    @Override
    Object visitLocalVariableDeclarationStatement(Java8Parser.LocalVariableDeclarationStatementContext ctx) {
        return null
    }

    @Override
    Object visitLocalVariableDeclaration(Java8Parser.LocalVariableDeclarationContext ctx) {
        return null
    }

    @Override
    Object visitStatement(Java8Parser.StatementContext ctx) {
        return null
    }

    @Override
    Object visitStatementNoShortIf(Java8Parser.StatementNoShortIfContext ctx) {
        return null
    }

    @Override
    Object visitStatementWithoutTrailingSubstatement(Java8Parser.StatementWithoutTrailingSubstatementContext ctx) {
        return null
    }

    @Override
    Object visitEmptyStatement(Java8Parser.EmptyStatementContext ctx) {
        return null
    }

    @Override
    Object visitLabeledStatement(Java8Parser.LabeledStatementContext ctx) {
        return null
    }

    @Override
    Object visitLabeledStatementNoShortIf(Java8Parser.LabeledStatementNoShortIfContext ctx) {
        return null
    }

    @Override
    Object visitExpressionStatement(Java8Parser.ExpressionStatementContext ctx) {
        return null
    }

    @Override
    Object visitStatementExpression(Java8Parser.StatementExpressionContext ctx) {
        return null
    }

    @Override
    Object visitIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
        return null
    }

    @Override
    Object visitIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
        return null
    }

    @Override
    Object visitIfThenElseStatementNoShortIf(Java8Parser.IfThenElseStatementNoShortIfContext ctx) {
        return null
    }

    @Override
    Object visitAssertStatement(Java8Parser.AssertStatementContext ctx) {
        return null
    }

    @Override
    Object visitSwitchStatement(Java8Parser.SwitchStatementContext ctx) {
        return null
    }

    @Override
    Object visitSwitchBlock(Java8Parser.SwitchBlockContext ctx) {
        return null
    }

    @Override
    Object visitSwitchBlockStatementGroup(Java8Parser.SwitchBlockStatementGroupContext ctx) {
        return null
    }

    @Override
    Object visitSwitchLabels(Java8Parser.SwitchLabelsContext ctx) {
        return null
    }

    @Override
    Object visitSwitchLabel(Java8Parser.SwitchLabelContext ctx) {
        return null
    }

    @Override
    Object visitEnumConstantName(Java8Parser.EnumConstantNameContext ctx) {
        return null
    }

    @Override
    Object visitWhileStatement(Java8Parser.WhileStatementContext ctx) {
        return null
    }

    @Override
    Object visitWhileStatementNoShortIf(Java8Parser.WhileStatementNoShortIfContext ctx) {
        return null
    }

    @Override
    Object visitDoStatement(Java8Parser.DoStatementContext ctx) {
        return null
    }

    @Override
    Object visitForStatement(Java8Parser.ForStatementContext ctx) {
        return null
    }

    @Override
    Object visitForStatementNoShortIf(Java8Parser.ForStatementNoShortIfContext ctx) {
        return null
    }

    @Override
    Object visitBasicForStatement(Java8Parser.BasicForStatementContext ctx) {
        return null
    }

    @Override
    Object visitBasicForStatementNoShortIf(Java8Parser.BasicForStatementNoShortIfContext ctx) {
        return null
    }

    @Override
    Object visitForInit(Java8Parser.ForInitContext ctx) {
        return null
    }

    @Override
    Object visitForUpdate(Java8Parser.ForUpdateContext ctx) {
        return null
    }

    @Override
    Object visitStatementExpressionList(Java8Parser.StatementExpressionListContext ctx) {
        return null
    }

    @Override
    Object visitEnhancedForStatement(Java8Parser.EnhancedForStatementContext ctx) {
        return null
    }

    @Override
    Object visitEnhancedForStatementNoShortIf(Java8Parser.EnhancedForStatementNoShortIfContext ctx) {
        return null
    }

    @Override
    Object visitBreakStatement(Java8Parser.BreakStatementContext ctx) {
        return null
    }

    @Override
    Object visitContinueStatement(Java8Parser.ContinueStatementContext ctx) {
        return null
    }

    @Override
    Object visitReturnStatement(Java8Parser.ReturnStatementContext ctx) {
        return null
    }

    @Override
    Object visitThrowStatement(Java8Parser.ThrowStatementContext ctx) {
        return null
    }

    @Override
    Object visitSynchronizedStatement(Java8Parser.SynchronizedStatementContext ctx) {
        return null
    }

    @Override
    Object visitTryStatement(Java8Parser.TryStatementContext ctx) {
        return null
    }

    @Override
    Object visitCatches(Java8Parser.CatchesContext ctx) {
        return null
    }

    @Override
    Object visitCatchClause(Java8Parser.CatchClauseContext ctx) {
        return null
    }

    @Override
    Object visitCatchFormalParameter(Java8Parser.CatchFormalParameterContext ctx) {
        return null
    }

    @Override
    Object visitCatchType(Java8Parser.CatchTypeContext ctx) {
        return null
    }

    @Override
    Object visitFinally_(Java8Parser.Finally_Context ctx) {
        return null
    }

    @Override
    Object visitTryWithResourcesStatement(Java8Parser.TryWithResourcesStatementContext ctx) {
        return null
    }

    @Override
    Object visitResourceSpecification(Java8Parser.ResourceSpecificationContext ctx) {
        return null
    }

    @Override
    Object visitResourceList(Java8Parser.ResourceListContext ctx) {
        return null
    }

    @Override
    Object visitResource(Java8Parser.ResourceContext ctx) {
        return null
    }

    @Override
    Object visitPrimary(Java8Parser.PrimaryContext ctx) {
        return null
    }

    @Override
    Object visitPrimaryNoNewArray(Java8Parser.PrimaryNoNewArrayContext ctx) {
        return null
    }

    @Override
    Object visitPrimaryNoNewArray_lf_arrayAccess(Java8Parser.PrimaryNoNewArray_lf_arrayAccessContext ctx) {
        return null
    }

    @Override
    Object visitPrimaryNoNewArray_lfno_arrayAccess(Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext ctx) {
        return null
    }

    @Override
    Object visitPrimaryNoNewArray_lf_primary(Java8Parser.PrimaryNoNewArray_lf_primaryContext ctx) {
        return null
    }

    @Override
    Object visitPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(Java8Parser.PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx) {
        return null
    }

    @Override
    Object visitPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(Java8Parser.PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx) {
        return null
    }

    @Override
    Object visitPrimaryNoNewArray_lfno_primary(Java8Parser.PrimaryNoNewArray_lfno_primaryContext ctx) {
        return null
    }

    @Override
    Object visitPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(Java8Parser.PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx) {
        return null
    }

    @Override
    Object visitPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx) {
        return null
    }

    @Override
    Object visitClassInstanceCreationExpression(Java8Parser.ClassInstanceCreationExpressionContext ctx) {
        return null
    }

    @Override
    Object visitClassInstanceCreationExpression_lf_primary(Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx) {
        return null
    }

    @Override
    Object visitClassInstanceCreationExpression_lfno_primary(Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx) {
        return null
    }

    @Override
    Object visitTypeArgumentsOrDiamond(Java8Parser.TypeArgumentsOrDiamondContext ctx) {
        return null
    }

    @Override
    Object visitFieldAccess(Java8Parser.FieldAccessContext ctx) {
        return null
    }

    @Override
    Object visitFieldAccess_lf_primary(Java8Parser.FieldAccess_lf_primaryContext ctx) {
        return null
    }

    @Override
    Object visitFieldAccess_lfno_primary(Java8Parser.FieldAccess_lfno_primaryContext ctx) {
        return null
    }

    @Override
    Object visitArrayAccess(Java8Parser.ArrayAccessContext ctx) {
        return null
    }

    @Override
    Object visitArrayAccess_lf_primary(Java8Parser.ArrayAccess_lf_primaryContext ctx) {
        return null
    }

    @Override
    Object visitArrayAccess_lfno_primary(Java8Parser.ArrayAccess_lfno_primaryContext ctx) {
        return null
    }

    @Override
    Object visitMethodInvocation(Java8Parser.MethodInvocationContext ctx) {
        return null
    }

    @Override
    Object visitMethodInvocation_lf_primary(Java8Parser.MethodInvocation_lf_primaryContext ctx) {
        return null
    }

    @Override
    Object visitMethodInvocation_lfno_primary(Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        return null
    }

    @Override
    Object visitArgumentList(Java8Parser.ArgumentListContext ctx) {
        return null
    }

    @Override
    Object visitMethodReference(Java8Parser.MethodReferenceContext ctx) {
        return null
    }

    @Override
    Object visitMethodReference_lf_primary(Java8Parser.MethodReference_lf_primaryContext ctx) {
        return null
    }

    @Override
    Object visitMethodReference_lfno_primary(Java8Parser.MethodReference_lfno_primaryContext ctx) {
        return null
    }

    @Override
    Object visitArrayCreationExpression(Java8Parser.ArrayCreationExpressionContext ctx) {
        return null
    }

    @Override
    Object visitDimExprs(Java8Parser.DimExprsContext ctx) {
        return null
    }

    @Override
    Object visitDimExpr(Java8Parser.DimExprContext ctx) {
        return null
    }

    @Override
    Object visitConstantExpression(Java8Parser.ConstantExpressionContext ctx) {
        return null
    }

    @Override
    Object visitExpression(Java8Parser.ExpressionContext ctx) {
        return null
    }

    @Override
    Object visitLambdaExpression(Java8Parser.LambdaExpressionContext ctx) {
        return null
    }

    @Override
    Object visitLambdaParameters(Java8Parser.LambdaParametersContext ctx) {
        return null
    }

    @Override
    Object visitInferredFormalParameterList(Java8Parser.InferredFormalParameterListContext ctx) {
        return null
    }

    @Override
    Object visitLambdaBody(Java8Parser.LambdaBodyContext ctx) {
        return null
    }

    @Override
    Object visitAssignmentExpression(Java8Parser.AssignmentExpressionContext ctx) {
        return null
    }

    @Override
    Object visitAssignment(Java8Parser.AssignmentContext ctx) {
        return null
    }

    @Override
    Object visitLeftHandSide(Java8Parser.LeftHandSideContext ctx) {
        return null
    }

    @Override
    Object visitAssignmentOperator(Java8Parser.AssignmentOperatorContext ctx) {
        return null
    }

    @Override
    Object visitConditionalExpression(Java8Parser.ConditionalExpressionContext ctx) {
        return null
    }

    @Override
    Object visitConditionalOrExpression(Java8Parser.ConditionalOrExpressionContext ctx) {
        return null
    }

    @Override
    Object visitConditionalAndExpression(Java8Parser.ConditionalAndExpressionContext ctx) {
        return null
    }

    @Override
    Object visitInclusiveOrExpression(Java8Parser.InclusiveOrExpressionContext ctx) {
        return null
    }

    @Override
    Object visitExclusiveOrExpression(Java8Parser.ExclusiveOrExpressionContext ctx) {
        return null
    }

    @Override
    Object visitAndExpression(Java8Parser.AndExpressionContext ctx) {
        return null
    }

    @Override
    Object visitEqualityExpression(Java8Parser.EqualityExpressionContext ctx) {
        return null
    }

    @Override
    Object visitRelationalExpression(Java8Parser.RelationalExpressionContext ctx) {
        return null
    }

    @Override
    Object visitShiftExpression(Java8Parser.ShiftExpressionContext ctx) {
        return null
    }

    @Override
    Object visitAdditiveExpression(Java8Parser.AdditiveExpressionContext ctx) {
        return null
    }

    @Override
    Object visitMultiplicativeExpression(Java8Parser.MultiplicativeExpressionContext ctx) {
        return null
    }

    @Override
    Object visitUnaryExpression(Java8Parser.UnaryExpressionContext ctx) {
        return null
    }

    @Override
    Object visitPreIncrementExpression(Java8Parser.PreIncrementExpressionContext ctx) {
        return null
    }

    @Override
    Object visitPreDecrementExpression(Java8Parser.PreDecrementExpressionContext ctx) {
        return null
    }

    @Override
    Object visitUnaryExpressionNotPlusMinus(Java8Parser.UnaryExpressionNotPlusMinusContext ctx) {
        return null
    }

    @Override
    Object visitPostfixExpression(Java8Parser.PostfixExpressionContext ctx) {
        return null
    }

    @Override
    Object visitPostIncrementExpression(Java8Parser.PostIncrementExpressionContext ctx) {
        return null
    }

    @Override
    Object visitPostIncrementExpression_lf_postfixExpression(Java8Parser.PostIncrementExpression_lf_postfixExpressionContext ctx) {
        return null
    }

    @Override
    Object visitPostDecrementExpression(Java8Parser.PostDecrementExpressionContext ctx) {
        return null
    }

    @Override
    Object visitPostDecrementExpression_lf_postfixExpression(Java8Parser.PostDecrementExpression_lf_postfixExpressionContext ctx) {
        return null
    }

    @Override
    Object visitCastExpression(Java8Parser.CastExpressionContext ctx) {
        return null
    }

    @Override
    Object visit(ParseTree parseTree) {
        return null
    }

    @Override
    Object visitChildren(RuleNode ruleNode) {
        return null
    }

    @Override
    Object visitTerminal(TerminalNode terminalNode) {
        return null
    }

    @Override
    Object visitErrorNode(ErrorNode errorNode) {
        return null
    }
}
